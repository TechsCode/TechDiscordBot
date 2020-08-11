package me.TechsCode.TechDiscordBot.github;

import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;

import java.io.File;
import java.io.IOException;

public class GithubRelease {

    private File file = null;
    private GHRelease release;
    private GHAsset asset = null;

    public GithubRelease(GHRelease release) throws IOException {
        if(release.getAssets().size() > 0) {
            this.asset = release.getAssets().get(0);
            this.file = GitHubUtil.downloadFile(release.getTagName(), asset);
        }
        this.release = release;
    }

    public File getFile() {
        return file;
    }

    public GHRelease getRelease() {
        return release;
    }

    public GHAsset getAsset() {
        return asset;
    }
}