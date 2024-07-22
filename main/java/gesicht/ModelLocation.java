package gesicht;
public class ModelLocation {

    private String title;
    private String description;
    private byte[] videoData; // Changed type to byte[] for video data

    public ModelLocation(String title, String description, byte[] videoData) {
        this.title = title;
        this.description = description;
        this.videoData = videoData;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getVideoData() {
        return videoData;
    }

    public void setVideoData(byte[] videoData) {
        this.videoData = videoData;
    }
}
