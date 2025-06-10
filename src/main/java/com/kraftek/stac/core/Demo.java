package com.kraftek.stac.core;

/**
 * This demo class demonstrates the usage of STACClientDemo to:
 * 1. Fetch and download assets from a STAC collection.
 * 2. Search for items in a STAC collection based on parameters like bounding box and time range.
 * 3. Fetch collections, items, and assets dynamically.
 */
public class Demo {

    public static void main(String[] args) {
        try {
            // ------------------- Use Case 1: Fetch and Download Assets -------------------
            System.out.println("\n========== USE CASE 1: FETCH AND DOWNLOAD ASSETS ==========");
            String endpoint1 = "https://explorer.digitalearth.africa/stac";
            String collectionName1 = "io_lulc_v2"; // Specify the collection name
            String outputPath1 = "C:/Java/Stac_outputs"; // Path to save downloaded assets
            int maxPages1 = 5; // Limit to 5 pages of items

           // If you want to activate or deactivate the first use case, comment or uncomment these two lines.
            // System.out.println("Fetching and downloading assets for collection: " + collectionName1);
            // STACClientDemo.fetchAndDownloadAssets(endpoint1, collectionName1, outputPath1, maxPages1);




            // ------------------- Use Case 2: Search Items by spatial and temporal Parameters -------------------
            System.out.println("\n========== USE CASE 2: SEARCH ITEMS ==========");
            String endpoint2 = "https://earth-search.aws.element84.com/v1";
            String collectionName2 = "sentinel-2-l2a"; // Specify the collection name
            String bbox2 = "-79.762,40.496,-71.856,45.013"; // Bounding box: minLon, minLat, maxLon, maxLat
            String datetime2 = "2020-01-01T00:00:00Z/2025-12-31T23:59:59Z"; // Time range: start/end
            int maxPages2 = 5; // Limit to 5 pages of search results

            // If you want to activate or deactivate the first use case, comment or uncomment these two lines just below :
            // System.out.println("Searching items in collection: " + collectionName2);
            // STACClientDemo.searchItems(endpoint2, collectionName2, bbox2, datetime2, maxPages2);

            // ------------------- Use Case 3: Only Fetch Collections, Items, and Assets -------------------
            System.out.println("\n========== USE CASE 3: FETCH COLLECTIONS, ITEMS, AND ASSETS ==========");
            String endpoint3 = "https://explorer.digitalearth.africa/stac";
            String collectionName3 = null; // Leave null to fetch all collections
            String itemId3 = null; // Leave null to fetch all items in each collection
            String assetKey3 = null; // Leave null to fetch all assets in each item
            int maxPages3 = 5; // Limit to 5 pages of results per collection

            // Initialize the STAC client
            Authentication authNone = new Authentication();
            authNone.setType(AuthenticationType.NONE);
            STACClient client3 = new STACClient(endpoint3, authNone);

            // Fetch collections, items, and assets dynamically
            System.out.println("Starting STAC hierarchy fetch...");
            STACFetcher.fetchHierarchy(client3, collectionName3, itemId3, assetKey3, maxPages3);
            System.out.println("STAC hierarchy fetch completed.");

        } catch (Exception e) {
            System.err.println("An error occurred during the demo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
