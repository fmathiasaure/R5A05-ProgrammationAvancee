# Se connecte a l'API et range le token dans $h pour les requetes suivantes.
#
# A lancer avec un point devant, pour que $h reste disponible dans ton terminal :
#   . .\connexion.ps1                                  # moderateur defini dans application.properties
#   . .\connexion.ps1 -nom entraineur_psg -motDePasse xxx
#
# Ensuite, ajoute -Headers $h a chaque requete :
#   Invoke-RestMethod -Uri http://localhost:8080/joueur -Headers $h

param(
    [string]$nom,
    [string]$motDePasse,
    [string]$base = "http://localhost:8080"
)

# Sans identifiants fournis, on prend le moderateur initial de application.properties
if (-not $nom -or -not $motDePasse) {
    $fichier = Join-Path $PSScriptRoot "src\main\resources\application.properties"
    $props = Get-Content $fichier
    $nom = ($props | Select-String '^r5a05\.app\.moderateur\.nom=(.*)').Matches[0].Groups[1].Value
    $motDePasse = ($props | Select-String '^r5a05\.app\.moderateur\.motDePasse=(.*)').Matches[0].Groups[1].Value
}

$body = @{ nomUtilisateur = $nom; motDePasse = $motDePasse } | ConvertTo-Json

try {
    $reponse = Invoke-RestMethod -Uri "$base/auth/login" -Method Post -ContentType "application/json" -Body $body
}
catch {
    Write-Host "Connexion refusee : verifie que l'application tourne et que les identifiants sont bons." -ForegroundColor Red
    return
}

$global:h = @{ Authorization = "Bearer $($reponse.token)" }

Write-Host "Connecte en tant que $($reponse.username) ($($reponse.roles -join ', '))." -ForegroundColor Green
Write-Host "Le token est dans `$h : ajoute -Headers `$h a tes requetes. Il expire dans 1 heure."
