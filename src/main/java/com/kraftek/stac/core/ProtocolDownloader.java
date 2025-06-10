package com.kraftek.stac.core;
import java.nio.file.Path;


public interface ProtocolDownloader {
    void download(String assetUrl, Path destination) throws Exception;
}