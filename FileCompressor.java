import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.CRC32;
import java.util.zip.Deflater;

public class FileCompressor {
    private static final byte[] GZIP_HEADER = new byte[]{31, -117, 8, 0, 0, 0, 0, 0, 0, -1};

    public static byte[] compressFile(byte[] dataBlock) throws IOException {
        //byte[] data = Files.readAllBytes(Paths.get(filename));

        CRC32 crc = new CRC32();
        crc.update(dataBlock);
        long checksum = crc.getValue();
        int uncompressedLength = dataBlock.length;

        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        deflater.setInput(dataBlock);
        deflater.finish();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        baos.write(GZIP_HEADER);

        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int bytesCompressed = deflater.deflate(buffer);
            //bos.write(buffer, 0, bytesCompressed);
            baos.write(buffer, 0, bytesCompressed);
        }

        // Write GZIP footer
        // writeIntLittleEndian(bos, (int) checksum);
        // writeIntLittleEndian(bos, uncompressedLength);

        writeIntLittleEndian(baos, (int) checksum);
        writeIntLittleEndian(baos, uncompressedLength);


        deflater.end();
        return baos.toByteArray();

        // bos.flush(); // Ensure all data is sent to stdout
        // bos.close(); // Close the stream to prevent any further writing

    }

    private static void writeIntLittleEndian(ByteArrayOutputStream baos, int value) throws IOException {
        baos.write(value & 0xFF);
        baos.write((value >> 8) & 0xFF);
        baos.write((value >> 16) & 0xFF);
        baos.write((value >> 24) & 0xFF);
    }
}