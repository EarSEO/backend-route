package com.earseo.route.service;

import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;
import com.graphhopper.json.Statement;
import com.graphhopper.util.CustomModel;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Arrays;

@Service
public class GraphHopperService {

    @Value("${graphhopper.osm-cdn-url}")
    private String osmCdnUrl;


    private static final String PROFILE_NAME = "foot_custom";
    private static final String PBF_FILENAME = "downloaded_seoul.osm.pbf";
    private static final String TEMP_PBF_FILE_PATH = System.getProperty("java.io.tmpdir") + File.separator + PBF_FILENAME;

    private GraphHopper hopper;

    @PostConstruct
    public void init() throws IOException {
        File cacheDir = new File("./graph-cache");
        File pbfFile = new File(TEMP_PBF_FILE_PATH);

        if (!cacheDir.exists() || cacheDir.list().length == 0 || !pbfFile.exists()) {
            if (!cacheDir.exists()) cacheDir.mkdirs();
            downloadFile(osmCdnUrl, pbfFile);
        }

        hopper = new GraphHopper();
        hopper.setOSMFile(pbfFile.getAbsolutePath());
        hopper.setGraphHopperLocation("./graph-cache");

        CustomModel customModel = new CustomModel();
        Statement avoidDestinationRoads = Statement.If("road_access == DESTINATION", Statement.Op.MULTIPLY, "0");
        customModel.addToPriority(avoidDestinationRoads);

        Profile profile = new Profile(PROFILE_NAME)
                .setVehicle("foot")
                .setWeighting("custom")
                .setCustomModel(customModel);

        hopper.setProfiles(Arrays.asList(profile));

        hopper.importOrLoad();

    }

    private void downloadFile(String urlStr, File file) throws IOException {
        URL website = new URL(urlStr);
        try (ReadableByteChannel rbc = Channels.newChannel(website.openStream());
             FileOutputStream fos = new FileOutputStream(file)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }



    public GraphHopper getHopper() {
        return hopper;
    }
}