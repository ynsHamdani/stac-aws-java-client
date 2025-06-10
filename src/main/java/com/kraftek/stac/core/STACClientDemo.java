package com.kraftek.stac.core;

import com.kraftek.stac.core.model.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class STACClientDemo {

    /**
     * Fetches and downloads all assets from a specified collection and endpoint.
     *
     * @param endpoint      The STAC endpoint.
     * @param collectionName The collection to fetch items from.
     * @param outputPath    The local directory to save the downloaded assets.
     * @param maxPages      The maximum number of pages to fetch.
     */
    public static void fetchAndDownloadAssets(String endpoint, String collectionName, String outputPath, int maxPages) {
        try {
            Authentication authNone = new Authentication();
            authNone.setType(AuthenticationType.NONE);

            // Initialize the STAC client
            S3Downloader s3Downloader = new S3Downloader(null, null);
            STACClientV2 client = new STACClientV2(endpoint, authNone, s3Downloader);

            // Create the download folder
            Path downloadFolder = Paths.get(outputPath, collectionName);
            Files.createDirectories(downloadFolder);
            System.out.println("Downloading assets to: " + downloadFolder);

            // Pagination variables
            int page = 1;
            int limit = 10;
            boolean hasMorePages = true;

            // Loop through items in the collection
            while (hasMorePages && page <= maxPages) {
                System.out.println("Fetching page: " + page);

                // Fetch items from the collection
                ItemCollection items = client.listItems(collectionName, page, limit);
                if (items.getFeatures() == null || items.getFeatures().isEmpty()) {
                    System.out.println("No items found.");
                    break;
                }

                // Process each item
                for (Item item : items.getFeatures()) {
                    System.out.println("\nProcessing item: " + item.getId());
                    Map<String, Asset> assets = item.getAssets();

                    for (Map.Entry<String, Asset> assetEntry : assets.entrySet()) {
                        Asset asset = assetEntry.getValue();
                        if (asset.getHref() != null && (asset.getHref().startsWith("http") || asset.getHref().startsWith("s3"))) {
                            try {
                                System.out.println("Downloading asset: " + asset.getHref());
                                client.download(asset, downloadFolder);
                            } catch (Exception e) {
                                System.err.println("Failed to download asset: " + asset.getHref());
                                e.printStackTrace();
                            }
                        } else {
                            System.out.println("Asset has no valid download link.");
                        }
                    }
                }

                // Check if there are more pages
                PageContext context = items.getContext();
                if (context != null && context.getReturned() < limit) {
                    hasMorePages = false;
                } else {
                    page++;
                }
            }

            System.out.println("All assets downloaded successfully.");
        } catch (Exception e) {
            System.err.println("Error during asset download: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Performs a search on the STAC catalog using specified parameters.
     *
     * @param endpoint      The STAC endpoint.
     * @param collectionName The collection to search in.
     * @param bbox          The bounding box for the search (comma-separated string: "minLon,minLat,maxLon,maxLat").
     * @param datetime      The time range for the search (ISO 8601 interval format: "start/end").
     * @param maxPages      The maximum number of pages to fetch.
     */
    public static void searchItems(String endpoint, String collectionName, String bbox, String datetime, int maxPages) {
        try {
            Authentication authNone = new Authentication();
            authNone.setType(AuthenticationType.NONE);

            // Initialize the STAC client
            STACClient client = new STACClient(endpoint, authNone);

            // Set search parameters
            Map<String, Object> searchParams = new HashMap<>();
            if (bbox != null) {
                searchParams.put("bbox", bbox);
            }
            if (datetime != null) {
                searchParams.put("datetime", datetime);
            }

            // Pagination variables
            int page = 1;
            int limit = 10;
            boolean hasMorePages = true;

            // Loop through search results
            while (hasMorePages && page <= maxPages) {
                System.out.println("Fetching page: " + page);

                // Fetch search results
                ItemCollection items = client.search(collectionName, searchParams, page, limit);
                if (items.getFeatures() == null || items.getFeatures().isEmpty()) {
                    System.out.println("No items found for the specified search parameters.");
                    break;
                }

                // Process each item
                for (Item item : items.getFeatures()) {
                    System.out.println("\nProcessing item: " + item.getId());
                    Map<String, Asset> assets = item.getAssets();

                    for (Map.Entry<String, Asset> assetEntry : assets.entrySet()) {
                        Asset asset = assetEntry.getValue();
                        System.out.println("  Asset Name: " + assetEntry.getKey());
                        System.out.println("  Asset Link: " + asset.getHref());
                    }
                }

                // Check if there are more pages
                PageContext context = items.getContext();
                if (context != null && context.getReturned() < limit) {
                    hasMorePages = false;
                } else {
                    page++;
                }
            }

            System.out.println("Search completed.");
        } catch (Exception e) {
            System.err.println("Error during search: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
