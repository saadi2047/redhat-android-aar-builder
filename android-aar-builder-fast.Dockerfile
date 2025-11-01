# 🧱 Base image
FROM openjdk:21-jdk-slim

# 📦 Install basic tools
RUN apt-get update && apt-get install -y wget unzip git curl && \
    rm -rf /var/lib/apt/lists/*

# 🧰 Android SDK setup
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Download and properly place Android command line tools
RUN mkdir -p $ANDROID_HOME/cmdline-tools && \
    cd $ANDROID_HOME/cmdline-tools && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip && \
    unzip -q commandlinetools-linux-11076708_latest.zip -d temp && \
    mv temp/cmdline-tools latest && \
    rm -rf temp commandlinetools-linux-11076708_latest.zip

# 🧩 Install SDK components using sdkmanager
RUN yes | $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --licenses && \
    $ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0"

# 💾 Prepare workspace + permissions
RUN mkdir -p /workspace/.gradle /workspace/tmp_home/.android
RUN chmod -R 777 /opt/android-sdk /workspace

WORKDIR /workspace
CMD ["/bin/bash"]
