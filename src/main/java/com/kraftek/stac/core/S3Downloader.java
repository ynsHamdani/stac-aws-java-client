package com.kraftek.stac.core;

import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.net.URI;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class S3Downloader implements ProtocolDownloader {

    private final AwsCredentialsProvider credentialsProvider;

    /**
     * Constructor for S3Downloader with credential options.
     *
     * @param accessKey  AWS Access Key (set to null for anonymous access)
     * @param secretKey  AWS Secret Key (set to null for anonymous access)
     */
    public S3Downloader(String accessKey, String secretKey) {
        if (accessKey != null && secretKey != null) {
            // Use user-provided credentials
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
            this.credentialsProvider = StaticCredentialsProvider.create(awsCredentials);
        } else {
            // Use anonymous credentials for public access
            this.credentialsProvider = AnonymousCredentialsProvider.create();
        }
    }

    @Override
    public void download(String assetUrl, Path destination) throws Exception {
        // Parse the S3 URL
        S3Resource resource = parseS3Url(assetUrl);

        // Resolve the bucket's region
        Region bucketRegion = S3RegionResolver.resolveBucketRegion(resource.getBucketName(), resource.getObjectKey());

        // Create an S3 client
        try (S3Client s3 = S3Client.builder()
                .region(bucketRegion)
                .credentialsProvider(credentialsProvider)
                .build()) {

            // Define the GetObjectRequest
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(resource.getBucketName())
                    .key(resource.getObjectKey())
                    .build();

            // Download the object
            s3.getObject(request, destination);
            System.out.println("Download completed: " + destination);
        }
    }

    private S3Resource parseS3Url(String assetUrl) throws Exception {
        // Use regex to extract bucket name and object key
        Pattern pattern = Pattern.compile("s3://([^/]+)/(.+)");
        Matcher matcher = pattern.matcher(assetUrl);

        if (matcher.matches()) {
            String bucketName = matcher.group(1);
            String objectKey = matcher.group(2);
            return new S3Resource(bucketName, objectKey);
        } else {
            throw new IllegalArgumentException("Invalid S3 URL: " + assetUrl);
        }
    }

    // Helper class to encapsulate S3 resource details
    private static class S3Resource {
        private final String bucketName;
        private final String objectKey;

        public S3Resource(String bucketName, String objectKey) {
            this.bucketName = bucketName;
            this.objectKey = objectKey;
        }

        public String getBucketName() {
            return bucketName;
        }

        public String getObjectKey() {
            return objectKey;
        }
    }
}
