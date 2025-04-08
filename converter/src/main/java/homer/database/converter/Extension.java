package homer.database.converter;

public enum Extension {

    CSV,
    HDBB;  // HomerDataBaseBackup

    @Override
    public String toString() {
        return "." + super.toString();
    }

    public boolean isFilePathEndsWithExtension(String filePath) {
        return filePath.toUpperCase().endsWith(this.toString().toUpperCase());
    }

    public String appendToFilePath(String filePath) {
        if (!isFilePathEndsWithExtension(filePath)) {
            filePath += this.toString();
        }
        return filePath;
    }

    public String removeFromFilePath(String filePath) {
        if (isFilePathEndsWithExtension(filePath)) {
            filePath = filePath.replace(this.toString(), "");
        }
        return filePath;
    }
}
