# Remplit une feuille de match : 11 titulaires en 4-3-3 + 7 remplacants, puis valide.
# L'application doit etre demarree et le script schema-feuille-match.sql execute.
#
# Exemples :
#   .\remplir-feuille.ps1                      # match 1, equipe 1
#   .\remplir-feuille.ps1 -matchId 1 -equipeId 2

param(
    [int]$matchId = 1,
    [int]$equipeId = 1,
    [string]$base = "http://localhost:8080"
)

$ErrorActionPreference = "Stop"

# Postes attendus par la formation 4-3-3 et composition du banc
$quotasTitulaires = [ordered]@{ GARDIEN = 1; DEFENSEUR = 4; MILIEU = 3; ATTAQUANT = 3 }
$quotasBanc       = [ordered]@{ GARDIEN = 1; DEFENSEUR = 2; MILIEU = 2; ATTAQUANT = 2 }

$script:compteurReserve = 0

function Get-MessageErreur($erreur) {
    if ($erreur.ErrorDetails -and $erreur.ErrorDetails.Message) {
        return $erreur.ErrorDetails.Message
    }
    return $erreur.Exception.Message
}

# Convertit le poste libre du joueur ("Defenseur", "Défenseur"...) vers l'enum attendu
function Convert-Poste($poste) {
    if (-not $poste) { return $null }
    $p = $poste.ToUpper() -replace '[ÉÈÊË]', 'E' -replace '[ÀÂÄ]', 'A'
    switch -Regex ($p) {
        '^GARDIEN'   { return 'GARDIEN' }
        '^DEFENSEUR' { return 'DEFENSEUR' }
        '^MILIEU'    { return 'MILIEU' }
        '^ATTAQUANT' { return 'ATTAQUANT' }
        default      { return $null }
    }
}

# Cree un joueur de reserve quand l'effectif ne suffit pas
function New-JoueurReserve($poste) {
    $script:compteurReserve++
    $body = @{
        nom           = "Reserve$($script:compteurReserve)"
        prenom        = "Joueur"
        dateNaissance = "2000-01-01"
        taille        = 180
        poids         = 75.00
        poste         = $poste
        noteMoyenne   = 6.00
        nbMatchsJoues = 0
        statut        = "ACTIF"
        equipe        = @{ id = $equipeId }
    } | ConvertTo-Json -Depth 3

    $cree = Invoke-RestMethod -Uri "$base/joueur" -Method Post -ContentType "application/json; charset=utf-8" -Body $body
    Write-Host "  + joueur cree : $($cree.nom) ($poste, id $($cree.id))" -ForegroundColor DarkYellow
    return $cree
}

# --- 1. Recuperer ou creer la feuille ----------------------------------------

$reponse = Invoke-RestMethod -Uri "$base/feuille"
$feuilles = @($reponse)
$feuille = $feuilles | Where-Object { $_.matchId -eq $matchId -and $_.equipeId -eq $equipeId } | Select-Object -First 1

if ($feuille) {
    Write-Host "Feuille existante : id $($feuille.id) ($($feuille.equipeNom))" -ForegroundColor Green
} else {
    $feuille = Invoke-RestMethod -Uri "$base/feuille?matchId=$matchId&equipeId=$equipeId" -Method Post
    Write-Host "Feuille creee : id $($feuille.id) ($($feuille.equipeNom))" -ForegroundColor Green
}
$feuilleId = $feuille.id

# Une feuille validee est verrouillee : la rouvrir pour pouvoir la modifier
if ($feuille.validee) {
    $feuille = Invoke-RestMethod -Uri "$base/feuille/devalider?id=$feuilleId" -Method Post
    Write-Host "Feuille devalidee pour modification." -ForegroundColor Yellow
}

# Repartir d'une feuille vide, pour que le script soit rejouable
$dejaTitulaires = @($feuille.titulaires)
$dejaRemplacants = @($feuille.remplacants)
foreach ($s in ($dejaTitulaires + $dejaRemplacants)) {
    if ($s -and $s.joueurId) {
        Invoke-RestMethod -Uri "$base/feuille/selection?id=$feuilleId&joueurId=$($s.joueurId)" -Method Delete | Out-Null
    }
}

# --- 2. Composer l'equipe ----------------------------------------------------

# Attention : en PowerShell 7.6, Invoke-RestMethod renvoie le tableau JSON comme UN SEUL
# objet. Ecrire @(Invoke-RestMethod ...) donnerait un tableau imbrique de 1 element.
# Il faut passer par une variable intermediaire avant d'appeler @().
$reponse = Invoke-RestMethod -Uri "$base/feuille/disponibles?id=$feuilleId"
$disponibles = @($reponse)
Write-Host "$($disponibles.Count) joueur(s) actif(s) disponible(s)." -ForegroundColor Cyan

$selections = @()
$restants = $disponibles

function Select-Joueurs($quotas, $titulaire) {
    foreach ($poste in $quotas.Keys) {
        for ($i = 0; $i -lt $quotas[$poste]; $i++) {
            $joueur = $script:restants | Where-Object { (Convert-Poste $_.poste) -eq $poste } | Select-Object -First 1

            if ($joueur) {
                $script:restants = @($script:restants | Where-Object { $_.id -ne $joueur.id })
            } else {
                $joueur = New-JoueurReserve $poste
            }

            $script:selections += [pscustomobject]@{
                joueurId  = $joueur.id
                nom       = "$($joueur.prenom) $($joueur.nom)"
                poste     = $poste
                titulaire = $titulaire
            }
        }
    }
}

Select-Joueurs $quotasTitulaires $true
Select-Joueurs $quotasBanc $false

# --- 3. Envoyer les selections -----------------------------------------------

$ok = 0
$echecs = 0

foreach ($s in $selections) {
    $body = @{ joueurId = $s.joueurId; poste = $s.poste; titulaire = $s.titulaire } | ConvertTo-Json
    $role = if ($s.titulaire) { "titulaire " } else { "remplacant" }

    try {
        Invoke-RestMethod -Uri "$base/feuille/selection?id=$feuilleId" -Method Post `
            -ContentType "application/json; charset=utf-8" -Body $body | Out-Null
        Write-Host "OK    $role $($s.poste.PadRight(10)) $($s.nom)" -ForegroundColor Green
        $ok++
    }
    catch {
        Write-Host "ECHEC $role $($s.poste.PadRight(10)) $($s.nom) : $(Get-MessageErreur $_)" -ForegroundColor Red
        $echecs++
    }
}

Write-Host ""
Write-Host "$ok selection(s) enregistree(s), $echecs echec(s)." -ForegroundColor Cyan

# --- 4. Controler puis valider -----------------------------------------------

$controle = Invoke-RestMethod -Uri "$base/feuille/controle?id=$feuilleId"

if (-not $controle.valide) {
    Write-Host "Feuille non validable :" -ForegroundColor Red
    $controle.erreurs | ForEach-Object { Write-Host "  - $_" -ForegroundColor Red }
    exit 1
}

try {
    $validee = Invoke-RestMethod -Uri "$base/feuille/valider?id=$feuilleId" -Method Post
    Write-Host "Feuille $feuilleId validee : $($validee.titulaires.Count) titulaires, $($validee.remplacants.Count) remplacants." -ForegroundColor Green
}
catch {
    Write-Host "Validation refusee : $(Get-MessageErreur $_)" -ForegroundColor Red
    exit 1
}

Write-Host "Detail : $base/feuille?id=$feuilleId"
