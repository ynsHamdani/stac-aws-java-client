package com.kraftek.stac.core;

import com.kraftek.stac.core.model.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class STACClientV2 extends STACClient {
    private final S3Downloader s3Downloader;

    /**
     * Constructor for STACClientV2
     *
     * @param baseURL        The URL of the STAC web service
     * @param authentication The authentication scheme for HTTP
     * @param s3Downloader   The S3 downloader instance
     * @throws MalformedURLException if the baseURL is invalid
     */
    public STACClientV2(String baseURL, Authentication authentication, S3Downloader s3Downloader) throws MalformedURLException {
        super(baseURL, authentication);
        this.s3Downloader = s3Downloader;
    }

    @Override
    public void download(Asset asset, Path folder) throws IOException {
        String href = asset.getHref();
        download(href, folder);
    }

    @Override
    public void download(String href, Path folder) throws IOException {
        try {
            URI uri = new URI(href);

            switch (uri.getScheme().toLowerCase()) {
                case "http":
                case "https":
                    // Use existing HTTP download logic
                    super.download(href, folder);
                    break;

                case "s3":
                    // Use S3Downloader for S3 downloads
                    String fileName = extractFileName(href);
                    Path destination = folder.resolve(fileName);
                    s3Downloader.download(href, destination);
                    System.out.println("Downloaded S3 asset to: " + destination);
                    break;

                default:
                    throw new UnsupportedOperationException("Unsupported protocol: " + uri.getScheme());
            }
        } catch (Exception e) {
            throw new IOException("Failed to download asset: " + href, e);
        }
    }

    @Override
    public void download(Item item, Path folder) throws IOException {
        final Map<String, Asset> assets = item.getAssets();
        if (assets != null && !assets.isEmpty()) {
            Path targetFolder = folder.resolve(item.getId());
            Files.createDirectories(targetFolder);

            for (Asset asset : assets.values()) {
                download(asset, targetFolder);
            }
        }
    }

    private String extractFileName(String href) {
        return href.substring(href.lastIndexOf('/') + 1);
    }

    @Override
    public InputStream download(Asset asset) throws IOException {
        String href = asset.getHref();
        try {
            URI uri = new URI(href);

            switch (uri.getScheme().toLowerCase()) {
                case "http":
                case "https":
                    // Use HTTP for stream download
                    return super.download(asset);

                case "s3":
                    // S3 streaming is not implemented in this example but can be added if needed
                    throw new UnsupportedOperationException("S3 streaming not supported. Use file-based download.");
                default:
                    throw new UnsupportedOperationException("Unsupported protocol: " + uri.getScheme());
            }
        } catch (Exception e) {
            throw new IOException("Failed to stream asset: " + href, e);
        }
    }

    //download all assets in a collection with a single call
    public void downloadCollection(String collectionName, Path destination) throws IOException {
        ItemCollection items = listItems(collectionName);
        for (Item item : items.getFeatures()) {
            download(item, destination);
        }
    }
}