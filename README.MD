# stac-aws-java-client – Simple STAC Client for Java

**stac-aws-java-client** is a lightweight Java client for interacting with [SpatioTemporal Asset Catalog (STAC)](https://stacspec.org/) APIs. It provides convenient functionality for exploring, querying, and downloading geospatial assets. The client supports both **basic** and **token-based authentication** and includes a demo interface to illustrate typical use cases.

---

## ✨ Features

- Browse STAC **catalogs**, **collections**, and **items**
- Search collections using **bounding boxes** and **date ranges**
- Download **individual assets** or full STAC items
- Support for **Basic** and **Token** authentication
- Includes a demo interface with three real-world use cases

---

## 🚀 Demo Use Cases

The `Demo.java` class illustrates three common workflows:

### ✅ Use Case 1: Fetch and Download Assets from a Collection

```java
String endpoint = "https://stac.openeo.vito.be";
String collection = "ESA_WORLDCOVER_10M_2020_V1";
String outputPath = "C:/Java/Stac_outputs/Demo/vito_stac";
int maxPages = 5;

STACClientDemo.fetchAndDownloadAssets(endpoint, collection, outputPath, maxPages);
```

### ✅ Use Case 2: SEARCH ITEMS

```java
System.out.println("\n========== USE CASE 2: SEARCH ITEMS ==========");

String endpoint2 = "https://stac.openeo.vito.be";
String collectionName2 = "sentinel-2-l2a"; // Collection to search
String bbox2 = "-79.762,40.496,-71.856,45.013"; // minLon, minLat, maxLon, maxLat
String datetime2 = "2020-01-01T00:00:00Z/2025-12-31T23:59:59Z"; // Time range
int maxPages2 = 5; // Max pages of results to fetch

// Uncomment to activate
// System.out.println("Searching items in collection: " + collectionName2);
// STACClientDemo.searchItems(endpoint2, collectionName2, bbox2, datetime2, maxPages2);

```

### ✅ Use Case 3g: FETCH COLLECTIONS, ITEMS, AND ASSETS

```java
System.out.println("\n========== USE CASE 3: FETCH COLLECTIONS, ITEMS, AND ASSETS ==========");

String endpoint3 = "https://stac.openeo.vito.be";
String collectionName3 = "TWI10_LAEA"; // Set to null to fetch all collections
String itemId3 = null; // Set to null to fetch all items
String assetKey3 = null; // Set to null to fetch all assets
int maxPages3 = 5; // Limit the number of pages per request

// Uncomment to activate
// Authentication authNone = new Authentication();
// authNone.setType(AuthenticationType.NONE);
// STACClient client3 = new STACClient(endpoint3, authNone);

// System.out.println("Starting STAC hierarchy fetch...");
// STACFetcher.fetchHierarchy(client3, collectionName3, itemId3, assetKey3, maxPages3);
// System.out.println("STAC hierarchy fetch completed.");
```

