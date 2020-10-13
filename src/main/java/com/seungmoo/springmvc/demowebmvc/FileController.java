package com.seungmoo.springmvc.demowebmvc;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;

@Slf4j
@Controller
public class FileController {
    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/file")
    public String fileUploadForm(Model model) { // redirect로 들어온 메시지는 model에 자동으로 담긴다.
        // Model parameter 선언만 해두면 자동으로 데이터가 binding 되고 Model이 view에 전달이 된다.
        return "/files/index";
    }

    @PostMapping("/file")
    public String fileUpload(@RequestParam("file") MultipartFile file, // RequestParam의 name은 form에서 보내는 name과 동일하게
                             RedirectAttributes attributes
                            ) {
        // 파일 저장하는 기능은 생략함
        log.info("file name : " + file.getName());
        log.info("file original name : " + file.getOriginalFilename());
        String message = file.getOriginalFilename() + " is uploaded";
        attributes.addFlashAttribute("message", message);
        return "redirect:/file";
    }

    // File Download는 ResponseEntity를 활용하여 구현
    @GetMapping("/file/{filename}")
    //@ResponseBody  ResponseEntity를 return 하는 handler의 경우 @ResponseBody 설정할 필요가 없음
    public ResponseEntity fileDownload(@PathVariable String filename) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + filename);
        File file = resource.getFile();

        Tika tika = new Tika();
        String mediaType = tika.detect(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, mediaType)
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
                .body(resource);
    }
}
