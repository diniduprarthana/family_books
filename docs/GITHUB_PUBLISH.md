# Publishing Family Books to GitHub

Use real commits only. Do not rewrite history to invent contribution.

## 1. Initialize (if needed)

```bash
git init
git add .
git commit -m "Describe the real milestone you completed"
```

## 2. Create GitHub repository

On GitHub: **New repository** → name e.g. `Family_Books` → do not auto-add README if local project already has one.

## 3. Connect and push

```bash
git branch -M main
git remote add origin https://github.com/<your-username>/Family_Books.git
git push -u origin main
```

## 4. Team contribution tips

- Each member commits their own work from their machine/account
- Use meaningful messages (`Add review ownership checks`, not `update`)
- Keep `data/*.txt` seed-friendly; avoid committing personal password experiments if they differ from demo seeds
- Never commit secrets

## 5. For viva

Be ready to show:

```bash
git log --oneline --graph --decorate
```
