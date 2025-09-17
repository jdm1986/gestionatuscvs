param(
    [Parameter(Mandatory=$false)] [string]$ApiKey,
    [Parameter(Mandatory=$false)] [string]$From = "info@gestionatuscv.es",
    [Parameter(Mandatory=$false)] [string]$Admin = "info@gestionatuscv.es",
    [switch]$Persist
)

$ErrorActionPreference = 'Stop'

function Ensure-Line {
    param([string]$Path, [string]$Key, [string]$Value)
    if (!(Test-Path $Path)) { New-Item -ItemType File -Path $Path -Force | Out-Null }
    $content = Get-Content -Path $Path -Raw
    if ($content -match "^(?im)${Key}=.*$") {
        $updated = [System.Text.RegularExpressions.Regex]::Replace($content, "^(?im)${Key}=.*$", "${Key}=${Value}")
        Set-Content -Path $Path -Value $updated -NoNewline
    } else {
        Add-Content -Path $Path -Value "${Key}=${Value}"
    }
}

if (-not $ApiKey) {
    $ApiKey = Read-Host -AsSecureString "Pega tu SENDGRID_API_KEY"
    $ApiKey = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($ApiKey))
}

$envPath = Join-Path -Path (Get-Location) -ChildPath ".env"

Ensure-Line -Path $envPath -Key "SENDGRID_API_KEY" -Value $ApiKey
Ensure-Line -Path $envPath -Key "APP_MAIL_FROM" -Value $From
Ensure-Line -Path $envPath -Key "APP_MAIL_ADMIN" -Value $Admin

Write-Host "Actualizado .env con claves: SENDGRID_API_KEY, APP_MAIL_FROM, APP_MAIL_ADMIN" -ForegroundColor Green

if ($Persist) {
    & setx SENDGRID_API_KEY "$ApiKey" | Out-Null
    & setx APP_MAIL_FROM "$From" | Out-Null
    & setx APP_MAIL_ADMIN "$Admin" | Out-Null
    Write-Host "Variables persistidas en el perfil de usuario (abre una nueva terminal)." -ForegroundColor Green
}

Write-Host "Listo. Ahora puedes ejecutar: docker compose up -d --build" -ForegroundColor Cyan

