package me.TechsCode.TechDiscordBot.github;

import me.TechsCode.TechDiscordBot.TechDiscordBot;
import org.kohsuke.github.GHAsset;
import org.kohsuke.github.GHRelease;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GithubRelease {

    private File file = null;
    private final GHRelease release;
    private GHAsset asset = null;

    public GithubRelease(GHRelease release) throws IOException {
        List<GHAsset> releaseAsset = release.listAssets().toList();
        if(releaseAsset.size() > 0) {
            this.asset = releaseAsset.get(0);
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