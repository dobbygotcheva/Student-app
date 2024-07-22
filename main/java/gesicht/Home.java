package gesicht;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Home extends JPanel {

    private List<ModelLocation> locations;
    private int index = 0;
    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;

    public Home() {
        init();
        testData();
    }

    private void init() {
        factory = new MediaPlayerFactory();
        mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
        Canvas canvas = new Canvas();
        mediaPlayer.videoSurface().set(factory.videoSurfaces().newVideoSurface(canvas));
        mediaPlayer.events().addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
            @Override
            public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
                if (newTime >= mediaPlayer.status().length() - 1000) {
                    mediaPlayer.controls().setPosition(0);
                }
            }
        });

        // Add the EmbeddedMediaPlayer component to the Home panel
        setLayout(new BorderLayout());
        add(canvas, BorderLayout.CENTER);
    }

    private void testData() {
        locations = new ArrayList<>();
        // Read the video file and convert its content to bytes
        byte[] videoData = readVideoFile("C:/Users/dobri/OneDrive/Desktop/demo/src/main/resources/static/listopad.mp4");
        locations.add(new ModelLocation("Lord of the Lost", "Lorelei", videoData));
    }

    private byte[] readVideoFile(String filePath) {
        try (InputStream inputStream = new FileInputStream(filePath);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void play(int index) {
        this.index = index;
        ModelLocation location = locations.get(index);
        if (mediaPlayer.status().isPlaying()) {
            mediaPlayer.controls().stop();
        }

        // Write video data to a temporary file
        File tempFile = null;
        FileOutputStream outputStream = null;
        try {
            tempFile = File.createTempFile("video", ".mp4");
            outputStream = new FileOutputStream(tempFile);
            outputStream.write(location.getVideoData());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Play video from the temporary file
        if (tempFile != null) {
            mediaPlayer.media().start(tempFile.getAbsolutePath());
            mediaPlayer.controls().play();
        }
    }

    public void stop() {
        mediaPlayer.controls().stop();
        mediaPlayer.release();
        factory.release();
    }
}
