package com.kraftek.stac.core;

import com.kraftek.stac.core.model.*;
import java.util.Map;

public class STACFetcher {

    public static void fetchHierarchy(STACClient client, String collectionName, String itemId, String assetKey, int maxPages) {
        try {
            if (collectionName == null) {
                System.out.println("Fetching all collections...");
                CollectionList collections;
                try {
                    collections = client.listCollections();
                } catch (Exception e) {
                    System.err.println("Failed to fetch collections: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }

                if (collections == null || collections.getCollections().isEmpty()) {
                    System.out.println("No collections found.");
                    return;
                }

                for (Collection collection : collections.getCollections()) {
                    try {
                        processCollection(client, collection, itemId, assetKey, maxPages);
                    } catch (Exception e) {
                        System.err.println("Failed to process collection: " + collection.getId() + " - " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("Fetching collection: " + collectionName);
                Collection collection;
                try {
                    collection = client.getCollection(collectionName);
                } catch (Exception e) {
                    System.err.println("Failed to fetch collection: " + collectionName + " - " + e.getMessage());
                    e.printStackTrace();
                    return;
                }

                if (collection == null) {
                    System.out.println("Collection not found: " + collectionName);
                    return;
                }

                processCollection(client, collection, itemId, assetKey, maxPages);
            }

            System.out.println("\nFinished processing.");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void processCollection(STACClient client, Collection collection, String itemId, String assetKey, int maxPages) {
        System.out.println("\nCollection ID: " + collection.getId());
        System.out.println("Description: " + collection.getDescription());

        if (itemId == null) {
            int page = 1;
            int limit = 10;
            boolean hasMorePages = true;

            while (hasMorePages && page <= maxPages) {
                try {
                    System.out.println("Fetching items for collection: " + collection.getId() + ", Page: " + page);
                    ItemCollection items = client.listItems(collection.getId(), page, limit);

                    if (items.getFeatures() == null || items.getFeatures().isEmpty()) {
                        System.out.println("No items found for collection: " + collection.getId());
                        break;
                    }

                    for (Item item : items.getFeatures()) {
                        try {
                            processItem(item, assetKey);
                        } catch (Exception e) {
                            System.err.println("Failed to process item: " + item.getId() + " - " + e.getMessage());
                            e.printStackTrace();
                        }
                    }

                    PageContext context = items.getContext();
                    if (context != null && context.getReturned() < limit) {
                        hasMorePages = false;
                    } else {
                        page++;
                    }
                } catch (Exception e) {
                    System.err.println("Failed to fetch items for collection: " + collection.getId() + ", Page: " + page + " - " + e.getMessage());
                    e.printStackTrace();
                    hasMorePages = false; // Skip to the next collection
                }
            }

            if (page > maxPages) {
                System.out.println("Reached maximum page limit for collection: " + collection.getId());
            }
        } else {
            System.out.println("Fetching item: " + itemId);
            try {
                Item item = client.getItem(collection.getId(), itemId);
                if (item == null) {
                    System.out.println("Item not found: " + itemId);
                } else {
                    processItem(item, assetKey);
                }
            } catch (Exception e) {
                System.err.println("Failed to fetch item: " + itemId + " - " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private static void processItem(Item item, String assetKey) {
        System.out.println("\n  Item ID: " + item.getId());
        System.out.println("  Item Geometry: " + item.getGeometry());

        Map<String, Asset> assets = item.getAssets();

        if (assetKey == null) {
            for (Map.Entry<String, Asset> assetEntry : assets.entrySet()) {
                printAsset(assetEntry.getKey(), assetEntry.getValue());
            }
        } else {
            Asset asset = assets.get(assetKey);
            if (asset == null) {
                System.out.println("    Asset not found: " + assetKey);
            } else {
                printAsset(assetKey, asset);
            }
        }
    }

    private static void printAsset(String assetName, Asset asset) {
        System.out.println("    Asset Name: " + assetName);
        System.out.println("    Asset Type: " + asset.getType());
        System.out.println("    Asset Link: " + asset.getHref());
    }
}
