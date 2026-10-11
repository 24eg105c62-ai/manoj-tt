$git = "$env:LOCALAPPDATA\Programs\Git\cmd\git.exe"
if (-not (Test-Path $git)) {
    $git = "git"
}

Write-Host "Using Git from: $git"
& $git --version

# Configure default user if not already set
$userName = & $git config --get user.name
if (-not $userName) {
    & $git config --global user.name "Dhanush Reddy"
    & $git config --global user.email "dhanushreddy@example.com"
}

# 1. Init if needed
if (-not (Test-Path "c:\skill track\.git")) {
    Write-Host "Initializing git repository..."
    & $git init
}

# 2. Set default branch to main
& $git branch -M main

# 3. Add remote
$remote = & $git remote get-url origin 2>$null
if (-not $remote) {
    Write-Host "Adding remote origin..."
    & $git remote add origin https://github.com/24eg105c62-ai/manoj-tt.git
} else {
    Write-Host "Setting remote origin to https://github.com/24eg105c62-ai/manoj-tt.git..."
    & $git remote set-url origin https://github.com/24eg105c62-ai/manoj-tt.git
}

# 4. Stage and commit
& $git add .
$status = & $git status --porcelain
if ($status) {
    Write-Host "Committing changes..."
    & $git commit -m "Pocket Skill Track full-stack application"
} else {
    Write-Host "No new changes to commit."
}

# 5. Push
Write-Host "Pushing to origin main..."
& $git push -u origin main
