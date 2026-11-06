package com.example.files_cloud_storage;

import org.springframework.boot.SpringApplication;

public class TestFilesCloudStorageApplication {

	public static void main(String[] args) {
		SpringApplication.from(FilesCloudStorageApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
