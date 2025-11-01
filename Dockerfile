###################################################################
# 🏗️ Stage 1 — Builder
# Based on official Red Hat UBI9 + OpenJDK 21 (build-capable)
###################################################################
#FROM registry.access.redhat.com/ubi9/openjdk-21 AS builder
#FROM registry.redhat.io/ubi9/openjdk-21 AS builder


# Use Red Hat UBI9 JDK (Developer Edition)
#FROM registry.redhat.io/ubi9/openjdk-21-devel AS builder
###################################################################
# 🏗️ Stage 1 — Android AAR Builder (Red Hat UBI9 + OpenJDK 21)
###################################################################
FROM registry.access.redhat.com/ubi9/openjdk-21 AS builder

# Environment setup
ENV LANG=en_US.UTF-8 \
    LC_ALL=en_US.UTF-8 \
    TZ=Asia/Kolkata \
    ANDROID_HOME=/opt/android-sdk \
    PATH=$PATH:/opt/android-sdk/cmdline-tools/latest/bin:/opt/android-sdk/platform-tools

# Root privileges
USER root

# Install tools (avoid curl conflict)
RUN microdnf install -y wget unzip git which bash shadow-utils && \
    microdnf clean all

# Install Android SDK Command-line tools (✅ fixed folder path)
RUN mkdir -p $ANDROID_HOME/cmdline-tools && \
    cd $ANDROID_HOME/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip -q commandlinetools-linux-11076708_latest.zip && \
    rm commandlinetools-linux-11076708_latest.zip && \
    mv cmdline-tools latest

# Accept licenses & install platform tools
RUN yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses || true && \
    $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager \
        "platform-tools" \
        "platforms;android-35" \
        "build-tools;35.0.0"

# Prepare workspace
WORKDIR /workspace

# Default command
CMD ["/bin/bash"]

