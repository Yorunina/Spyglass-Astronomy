package com.nettakrim.spyglass_astronomy;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

public class SpaceRenderingManager {
    private final BufferBuilder.RenderedBuffer starsBuffer;
    private boolean starsReady = false;

    private final BufferBuilder.RenderedBuffer constellationsBuffer;
    private boolean constellationsReady = false;

    private final BufferBuilder.RenderedBuffer drawingConstellationsBuffer;
    private boolean drawingReady = false;

    private final BufferBuilder.RenderedBuffer planetsBuffer;
    private boolean planetsReady = false;

    private static float heightScale = 1;

    public static boolean constellationsVisible;
    public static boolean starsVisible;
    public static boolean orbitingBodiesVisible;
    public static boolean oldStarsVisible;
    public static boolean starsAlwaysVisible;

    private float starVisibility;

    private boolean constellationsNeedsUpdate = true;

    private File data = null;
    private Path storagePath;
    private final String fileName;

    public SpaceRenderingManager() {
        storagePath = Minecraft.getInstance().gameDirectory.toPath().resolve(".spyglass_astronomy");
        fileName = storagePath + "/rendering.txt";

        constellationsVisible = true;
        starsVisible = true;
        orbitingBodiesVisible = true;
        oldStarsVisible = false;
        starsAlwaysVisible = false;

        starsBuffer = null;
        constellationsBuffer = null;
        drawingConstellationsBuffer = null;
        planetsBuffer = null;

        if (Files.exists(storagePath)) {
            data = new File(fileName);
            if (data.exists()) {
                loadData();
            }
        }
    }

    private void loadData() {
        try {
            if (data.createNewFile()) {
                return;
            }
            Scanner scanner = new Scanner(data);
            String s = scanner.nextLine();
            scanner.close();
            constellationsVisible = charTrue(s, 0);
            starsVisible = charTrue(s, 1);
            orbitingBodiesVisible = charTrue(s, 2);
            oldStarsVisible = charTrue(s, 3);
            starsAlwaysVisible = charTrue(s, 4);
        } catch (IOException e) {
            SpyglassAstronomyClient.LOGGER.info("Failed to load data");
        }
    }

    private boolean charTrue(String s, int index) {
        return index < s.length() && s.charAt(index) == '1';
    }

    public void saveData() {
        try {
            if (data == null) {
                Files.createDirectories(storagePath);
                data = new File(fileName);
            }
            FileWriter writer = new FileWriter(data);
            String s = (constellationsVisible ? "1" : "0") + (starsVisible ? "1" : "0") + (orbitingBodiesVisible ? "1" : "0") + (oldStarsVisible ? "1" : "0") + (starsAlwaysVisible ? "1" : "0");
            writer.write(s);
            writer.close();
        } catch (IOException e) {
            SpyglassAstronomyClient.LOGGER.info("Failed to save data");
        }
    }

    public void updateSpace(int ticks) {
        updateHeightScale();
        if (Constellation.selected != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || !SpyglassAstronomyClient.isHoldingSpyglass()) {
                Constellation.deselect();
                constellationsNeedsUpdate = true;
            }
        }
        if (constellationsNeedsUpdate) {
            updateConstellations();
            constellationsNeedsUpdate = false;
        }

        if (Star.selected != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || !SpyglassAstronomyClient.isHoldingSpyglass()) {
                Star.deselect();
            }
        }

        if (OrbitingBody.selected != null) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || !SpyglassAstronomyClient.isHoldingSpyglass()) {
                OrbitingBody.deselect();
            }
        }

        updateStars(ticks);
        updateOrbits(ticks);
    }

    public void scheduleConstellationsUpdate() {
        constellationsNeedsUpdate = true;
    }

    public void cancelDrawing() {
        drawingReady = false;
    }

    private void updateConstellations() {
        drawingReady = SpyglassAstronomyClient.isDrawingConstellation;

        if (SpyglassAstronomyClient.constellations.isEmpty()) {
            constellationsReady = false;
            return;
        }

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (Constellation constellation : SpyglassAstronomyClient.constellations) {
            constellation.setVertices(bufferBuilder, false);
        }

        constellationsReady = true;
    }

    private void updateStars(int ticks) {
        if (SpyglassAstronomyClient.stars.isEmpty()) {
            starsReady = false;
            return;
        }

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        for (Star star : SpyglassAstronomyClient.stars) {
            star.update(ticks);
            star.setVertices(bufferBuilder);
        }

        starsReady = true;
    }

    private void updateOrbits(int ticks) {
        if (SpyglassAstronomyClient.orbitingBodies.isEmpty()) {
            orbitingBodiesVisible = false;
            return;
        }

        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Long day = SpyglassAstronomyClient.getDay();
        float dayFraction = SpyglassAstronomyClient.getDayFraction();

        Vector3f referencePosition = SpyglassAstronomyClient.earthOrbit.getRotatedPositionAtGlobalTime(day, dayFraction, true);
        Vector3f normalisedReferencePosition = new Vector3f(referencePosition);
        normalisedReferencePosition.normalize();

        for (OrbitingBody orbitingBody : SpyglassAstronomyClient.orbitingBodies) {
            orbitingBody.update(ticks, referencePosition, normalisedReferencePosition, day, dayFraction);
            orbitingBody.setVertices(bufferBuilder);
        }

        planetsReady = true;
    }

    private void updateDrawingConstellation() {
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        SpyglassAstronomyClient.drawingConstellation.setVertices(bufferBuilder, true);
    }

    public void Render(PoseStack poseStack, Matrix4f projectionMatrix, float partialTick) {
        starVisibility = starsAlwaysVisible ? 1 : Minecraft.getInstance().level.getStarBrightness(partialTick) * (1.0f - Minecraft.getInstance().level.getRainLevel(partialTick));
        if (starVisibility > 0) {
            poseStack.popPose();
            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f));
            poseStack.mulPose(Axis.XP.rotationDegrees(SpyglassAstronomyClient.getStarAngle()));
            poseStack.mulPose(Axis.YP.rotationDegrees(45f));
            float colorScale = starVisibility + Math.min(heightScale, 0.5f);
            RenderSystem.setShaderColor(colorScale, colorScale, colorScale, starVisibility);

            if (starsVisible && starsReady) {
                renderBuffer(poseStack, projectionMatrix, starsBuffer);
            }

            if (constellationsVisible) {
                if (constellationsReady) {
                    renderBuffer(poseStack, projectionMatrix, constellationsBuffer);
                }
                if (SpyglassAstronomyClient.isDrawingConstellation || drawingReady) {
                    updateDrawingConstellation();
                    renderBuffer(poseStack, projectionMatrix, drawingConstellationsBuffer);
                }
            }

            if (orbitingBodiesVisible && planetsReady) {
                poseStack.popPose();
                poseStack.pushPose();
                poseStack.mulPose(Axis.ZP.rotationDegrees(SpyglassAstronomyClient.getPositionInOrbit(360f) * (1 - 1 / SpyglassAstronomyClient.earthOrbit.period) + 180));
                renderBuffer(poseStack, projectionMatrix, planetsBuffer);
            }
        }
    }

    private void renderBuffer(PoseStack poseStack, Matrix4f projectionMatrix, BufferBuilder.RenderedBuffer buffer) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.setShaderTexture(0, new ResourceLocation("textures/white.png"));

        // 应用模型视图矩阵
        RenderSystem.applyModelViewMatrix();
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorting.DISTANCE_TO_ORIGIN);

        buffer.release();
        BufferUploader.drawWithShader(buffer);

        RenderSystem.restoreProjectionMatrix();
    }
    public static void updateHeightScale() {
        heightScale = Mth.clamp((SpyglassAstronomyClient.getHeight() - 32f) / 256f, 0f, 1f);
    }

    public static float getHeightScale() {
        return heightScale;
    }

    public boolean starsCurrentlyVisible() {
        return starVisibility > 0;
    }
}