const fs = require('fs');

const CORDOVA_PLUGIN_NAME = "cordova-plugin-bilmobileads";
const enableAndroidXFalse = "android.useAndroidX=false";
const enableAndroidXTrue = "android.useAndroidX=true";
const enableJetifierFalse = "android.enableJetifier=false";
const enableJetifierTrue = "android.enableJetifier=true";
const gradlePropertiesPath = "./platforms/android/gradle.properties";

function log(message) {
    console.log(CORDOVA_PLUGIN_NAME + ": " + message);
}

function onError(error) {
    log(CORDOVA_PLUGIN_NAME + " - ERROR: " + error);
}

function run() {
    let gradleProperties = fs.readFileSync(gradlePropertiesPath);

    if (gradleProperties) {
        let updatedGradleProperties = false;
        gradleProperties = gradleProperties.toString();
        if (!gradleProperties.match(enableAndroidXTrue)) {
            updatedGradleProperties = true;
        }

        if (updatedGradleProperties) {
            gradleProperties = gradleProperties.replace(/ /g, '').replace(new RegExp(enableAndroidXFalse, 'g'), '').replace(new RegExp(enableAndroidXTrue, 'g'), '')
            gradleProperties = gradleProperties.replace(new RegExp(enableJetifierFalse, 'g'), '').replace(new RegExp(enableJetifierTrue, 'g'), '').replace(new RegExp('\n\n', 'g'), ' ')

            gradleProperties += "\n" + enableJetifierFalse;
            gradleProperties += "\n" + enableAndroidXTrue + "\n";

            fs.writeFileSync(gradlePropertiesPath, gradleProperties, 'utf8');
            log("Updated gradle.properties to enable AndroidX.");
        }
    } else {
        log("gradle.properties file not found!")
    }
}

module.exports = function () {
    return new Promise((resolve, reject) => {
        try {
            run();
            resolve();
        } catch (e) {
            onError("EXCEPTION: " + e.toString());
            reject(e);
        }
    });
};