#!/bin/bash
# ============================================================
# Surf Fountain Browser — Push to GitHub
# Run this on your PC or inside Replit shell.
# Usage: bash github-push.sh <your-github-username> <repo-name>
# Example: bash github-push.sh johndoe surf-fountain-browser
# ============================================================

set -e

RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; CYAN='\033[0;36m'; NC='\033[0m'
log()  { echo -e "${GREEN}[push]${NC} $*"; }
warn() { echo -e "${YELLOW}[warn]${NC} $*"; }
fail() { echo -e "${RED}[fail]${NC} $*"; exit 1; }
info() { echo -e "${CYAN}[info]${NC} $*"; }

GITHUB_USER="${1:-}"
REPO_NAME="${2:-surf-fountain-browser}"

if [ -z "$GITHUB_USER" ]; then
  echo ""
  echo "Usage: bash github-push.sh <github-username> [repo-name]"
  echo "Example: bash github-push.sh johndoe surf-fountain-browser"
  echo ""
  fail "GitHub username is required."
fi

# ── 1. Check we're in the right directory ───────────────────
if [ ! -f "gradlew" ]; then
  fail "Run this script from inside the surf-fountain-android/ directory."
fi

# ── 2. Init git if needed ────────────────────────────────────
if [ ! -d ".git" ]; then
  log "Initializing git repository..."
  git init
  git branch -M main
fi

# ── 3. Configure git ─────────────────────────────────────────
git config user.email "${GITHUB_USER}@users.noreply.github.com" 2>/dev/null || true
git config user.name "${GITHUB_USER}" 2>/dev/null || true

# ── 4. Stage all files ───────────────────────────────────────
log "Staging all files..."
git add -A
git status --short

# ── 5. Commit ────────────────────────────────────────────────
TIMESTAMP=$(date '+%Y-%m-%d %H:%M')
git commit -m "feat: Surf Fountain Browser v1.0 — full Android Studio project [$TIMESTAMP]" || {
  warn "Nothing new to commit."
}

# ── 6. Set remote ────────────────────────────────────────────
REMOTE_URL="https://github.com/${GITHUB_USER}/${REPO_NAME}.git"
if git remote get-url origin 2>/dev/null; then
  git remote set-url origin "$REMOTE_URL"
  log "Updated remote: $REMOTE_URL"
else
  git remote add origin "$REMOTE_URL"
  log "Added remote: $REMOTE_URL"
fi

# ── 7. Push ──────────────────────────────────────────────────
log "Pushing to GitHub..."
git push -u origin main

info ""
info "✅  Done! Your repo is live at:"
info "   https://github.com/${GITHUB_USER}/${REPO_NAME}"
info ""
info "GitHub Actions will automatically build the APK."
info "Find it at: https://github.com/${GITHUB_USER}/${REPO_NAME}/actions"
info ""
info "To build locally in Termux:"
info "  git clone https://github.com/${GITHUB_USER}/${REPO_NAME}.git"
info "  cd ${REPO_NAME}"
info "  bash termux-build.sh"
