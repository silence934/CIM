package xyz.nyist.controller;

import cn.hutool.core.io.file.FileNameUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import xyz.nyist.entity.ArtifactEntity;
import xyz.nyist.result.Result;
import xyz.nyist.result.ResultCode;
import xyz.nyist.service.ArtifactService;
import xyz.nyist.vo.ArtifactVO;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.UUID;

/**
 * @Author : fucong
 * @Date: 2021-03-14 10:24
 * @Description :
 */
@Slf4j
@RestController
public class FileController {

    @Value("${file-path}")
    private String path;
    private static final String ARTIFACT_PREFIX = "/cim-artifact";

    @Autowired
    private ArtifactService artifactService;


    /**
     * 上传文件
     *
     * @param file 文件对象
     * @return id
     */
    @PostMapping("/upload")
    public Result<ArtifactVO> upload(MultipartFile file, String type) {
        if (file == null) {
            return Result.failed(ResultCode.PARAM_ERROR, "请选择文件");
        }
        String fileName = FileNameUtil.getName(file.getOriginalFilename());
        String path = ARTIFACT_PREFIX + (type == null ? "" : ("/" + type)) + "/" + UUID.randomUUID().toString().substring(0, 8) + fileName;
        try {
            log.info("上传文件：" + this.path + path);
            FileUtils.copyInputStreamToFile(file.getInputStream(), new File(this.path + path));
            ArtifactEntity artifact = artifactService.create(path);
            return Result.success(ArtifactVO.forValue(artifact));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.failed(ResultCode.SYSTEM_EXECUTION_ERROR, "上传失败");
        }
    }

    /**
     * 下载文件
     *
     * @param response response
     * @param id       文件id
     */
    @GetMapping("/download")
    public Result<Object> download(HttpServletResponse response, Integer id) {
        ArtifactEntity artifact = artifactService.getById(id);
        try (InputStream in = new FileInputStream(artifact.getPath());
             OutputStream out = response.getOutputStream()) {
            response.reset();
            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(artifact.getFileName(), "UTF-8"));
            IOUtils.copy(in, out);
            return null;
        } catch (Exception e) {
            return Result.failed("下载文件失败-----id:" + id);
        }
    }

}
