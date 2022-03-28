
package br.com.segmedic.clubflex.support;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.CREATE_NEW;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtils {

   private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

   private FileUtils() {
      throw new IllegalStateException("Utility class");
   }

   public static Path salvarArquivoPastaTemporariaJVM(MultipartFile file) throws IOException {
      String tmpdir = System.getProperty("java.io.tmpdir");
      String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
      Path path = Paths.get(tmpdir + "/" + fileName);
      Files.createDirectories(path.getParent());
      Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

      return path;
   }

   public static String gerarNomeArquivoPastaTemporariaJVM(String filename) throws IOException {
      String tmpdir = System.getProperty("java.io.tmpdir");
      String fileName = StringUtils.cleanPath(filename);
      Path path = Paths.get(tmpdir + "/" + fileName);

      return path.toString();
   }

   /**
    * Método responsável por criar um arquivo TXT em pasta Temporaria JVM
    * 
    * @param Pasta - Pasta onde o arquivo sera criado.
    * @param conteudo - Conteudo do arquivo
    * @param nome - Nome do arquivo
    */
   public static void createFileTxtInPath(String conteudo, String nome) {
      String s = conteudo;
      byte data[] = s.getBytes();

      try {
         String tmpdir = System.getProperty("java.io.tmpdir");
         String fileName = StringUtils.cleanPath(nome);
         Path path = Paths.get(tmpdir.concat("/").concat(fileName).concat(".txt"));

         OutputStream out = new BufferedOutputStream(
            Files.newOutputStream(path, CREATE, CREATE_NEW));
         out.write(data, 0, data.length);

      }
      catch (IOException x) {
         LOGGER.error("Erro ao criar arquivo", x);
      }
   }
}
