# Dot-source from any directory: . ./scripts/Load-LocalEnv.ps1
# Parses data, never evaluates .env contents as PowerShell code.
param([string]$Path = (Join-Path $PSScriptRoot '..\.env'))
$ErrorActionPreference = 'Stop'
foreach ($line in Get-Content -LiteralPath $Path -Encoding UTF8) {
    if ([string]::IsNullOrWhiteSpace($line) -or $line.TrimStart().StartsWith('#')) { continue }
    if ($line -notmatch '^([A-Z][A-Z0-9_]*)=(.*)$') { throw 'Formato .env inválido; usar NOME=valor sem aspas.' }
    [Environment]::SetEnvironmentVariable($Matches[1], $Matches[2], 'Process')
}
