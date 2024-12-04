package homer.database.converter.backup;

enum BackupExtension {
    HDBB;

    @Override
    public String toString() {
        return "." + super.toString();
    }

    public boolean isFilePathEndsWithExtension(String filePath) {
        return filePath.endsWith(this.toString());
    }

    public String appendExtensionToBackupFilePathIfNotExists(String filePath) {
        if (!isFilePathEndsWithExtension(filePath)) {
            filePath += this.toString();
        }
        return filePath;
    }

    public String removeExtensionFromBackupFilePathIfExists(String filePath) {
        if (isFilePathEndsWithExtension(filePath)) {
            filePath = filePath.replace(this.toString(), "");
        }
        return filePath;
    }
}
