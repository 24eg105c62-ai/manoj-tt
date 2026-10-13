@echo off
echo ===================================================
echo   Pushing Pocket Skill Track to GitHub
echo ===================================================
git add .
git commit -m "Configure unified single connected deployment with Dockerfile and SPA routing"
git push origin main
echo ===================================================
echo   Done! Your repository is updated at:
echo   https://github.com/24eg105c62-ai/manoj-tt
echo ===================================================
pause
