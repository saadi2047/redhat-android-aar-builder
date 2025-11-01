cat << 'EOF' > README.md
# 🏗️ Red Hat UBI9 + OpenJDK 21 Android AAR Builder

This project builds Android `.aar` libraries using a **Red Hat UBI9 OpenJDK 21** base image.

## 🚀 Build the image

```bash
docker build -t redhat-android-aar:latest -f Dockerfile .


