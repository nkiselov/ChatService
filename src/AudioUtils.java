import javax.sound.sampled.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

public class AudioUtils {
    private static final AudioFormat format = new AudioFormat(8000.0f, 16, 1, true, true);

    public static void playAudio(InputStream is) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            System.exit(0);
        }
        SourceDataLine line = (SourceDataLine)AudioSystem.getLine(info);
        AudioInputStream ais = AudioSystem.getAudioInputStream(is);
        line.open(format);
        line.start();

        int bytesRead = 0;
        byte[] buffer = new byte[1024];
        while((bytesRead = ais.read(buffer)) != -1)
        {
            line.write(buffer, 0, bytesRead);
        }
        line.stop();
        line.drain();
        line.close();
        ais.close();
    }

    public static void recordAudio(Scanner scn, OutputStream os) throws LineUnavailableException, IOException {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            System.out.println("Line not supported");
            System.exit(0);
        }
        TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        AudioInputStream ais = new AudioInputStream(line);
        line.open(format);
        line.start();
        System.out.println("Started recording\r");
        Thread listen = new Thread(() -> {
            try {
                AudioSystem.write(ais, AudioFileFormat.Type.WAVE,os);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        listen.start();
        scn.nextLine();
        line.stop();
        line.drain();
        line.close();
        ais.close();
        System.out.println("Finished recording\r");
    }
}
