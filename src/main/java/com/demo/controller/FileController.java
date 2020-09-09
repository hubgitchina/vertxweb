package com.demo.controller;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.demo.annotation.RequestBody;
import com.demo.annotation.RequestMapping;
import com.demo.base.ControllerHandler;
import com.demo.enums.RequestMethod;

import io.vertx.core.Vertx;
import io.vertx.core.file.FileSystem;
import io.vertx.ext.web.FileUpload;

/**
 * @ClassName: FileController
 * @Description: TODO
 * @Author wangpeng
 * @Date 2020-08-18 14:03
 * @Version 1.0
 */
@Component
@RequestMapping("/file")
public class FileController {

	private final Logger logger = LoggerFactory.getLogger(FileController.class);

	@Value("${file.uploadFolder}")
	private String uploadFolder;

	@Autowired
	private Vertx vertx;

	@RequestBody
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public ControllerHandler upload() {

		return vertxRequest -> {

			System.out.println(System.getProperty("java.io.tmpdir"));

			Set<FileUpload> fileUploadSet = vertxRequest.getRoutingContext().fileUploads();

			FileSystem fs = vertx.fileSystem();
			fileUploadSet.forEach(fileUpload -> {
				fs.exists(uploadFolder, res -> {
					if (!res.result()) {
						logger.info("目录不存在，创建目录【{}】", uploadFolder);
						fs.mkdirs(uploadFolder, result -> {
							if (result.succeeded()) {
								logger.info("目录创建成功，开始上传");
								String path = uploadFolder + fileUpload.fileName();
								fs.copy(fileUpload.uploadedFileName(), path, upload -> {
									if (upload.succeeded()) {
										logger.info("文件上传成功");
										vertxRequest.buildVertxRespone().responeState(true);
									} else {
										logger.error("文件上传失败：{}", upload.cause().getMessage());
										vertxRequest.buildVertxRespone().responseFail(
												"文件上传失败：" + upload.cause().getMessage());
									}
								});
							} else {
								logger.error("目录创建失败：{}", result.cause().getMessage());
								vertxRequest.buildVertxRespone()
										.responseFail("目录创建失败：" + result.cause().getMessage());
							}
						});
					} else {
						logger.info("目录已存在，直接上传");
						String path = uploadFolder + fileUpload.fileName();
						fs.copy(fileUpload.uploadedFileName(), path, upload -> {
							if (upload.succeeded()) {
								logger.info("文件上传成功");
								vertxRequest.buildVertxRespone().responeState(true);
							} else {
								logger.error("文件上传失败：{}", upload.cause().getMessage());
								vertxRequest.buildVertxRespone()
										.responseFail("文件上传失败：" + upload.cause().getMessage());
							}
						});
					}
				});
			});
		};
	}

	@RequestBody
	@RequestMapping(value = "/download", method = RequestMethod.POST)
	public ControllerHandler download() {

		return vertxRequest -> {
			String fileName = "数据对比.docx";
			String path = uploadFolder + fileName;
			vertxRequest.buildVertxRespone().responseFile(fileName, path, null);
		};
	}
}
