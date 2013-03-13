package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.minecraft.client.Minecraft;
import com.friendsoverlay.fancygui.*;

public class GuiFancyMainMenu extends GuiFancyScreen {

	private List saveList;
	private GuiFancyRotatingBackground bg;

	/** The RNG used by the Main Menu Screen. */
	private static final Random rand = new Random();

	/** The splash message. */
	private String splashText = "missingno";
	private GuiButton buttonResetDemo;

	/** Timer used to rotate the panorama, increases every tick. */
	private int panoramaTimer = 0;

	/**
	 * Texture allocated for the current viewport of the main menu's panorama
	 * background.
	 */
	private int viewportTexture;
	private String field_92025_p;

	/** An array of all the paths to the panorama pictures. */
	private static final String[] titlePanoramaPaths = new String[] {
			"/title/bg/panorama0.png", "/title/bg/panorama1.png",
			"/title/bg/panorama2.png", "/title/bg/panorama3.png",
			"/title/bg/panorama4.png", "/title/bg/panorama5.png" };
	private int field_92024_r;
	private int field_92023_s;
	private int field_92022_t;
	private int field_92021_u;
	private int field_92020_v;
	private int field_92019_w;

	public GuiFancyMainMenu(Minecraft mc, GuiScreen oldScreen) {
		super(mc, oldScreen);
		BufferedReader var1 = null;

		try {
			ArrayList var2 = new ArrayList();
			var1 = new BufferedReader(
					new InputStreamReader(GuiMainMenu.class
							.getResourceAsStream("/title/splashes.txt"),
							Charset.forName("UTF-8")));
			String var3;

			while ((var3 = var1.readLine()) != null) {
				var3 = var3.trim();

				if (var3.length() > 0) {
					var2.add(var3);
				}
			}

			do {
				this.splashText = (String) var2.get(rand.nextInt(var2.size()));
			} while (this.splashText.hashCode() == 125780783);
		} catch (IOException var12) {
			;
		} finally {
			if (var1 != null) {
				try {
					var1.close();
				} catch (IOException var11) {
					;
				}
			}
		}
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		bg.Update();
	}

	/**
	 * Returns true if this GUI should pause the game when it is displayed in
	 * single-player
	 */
	public boolean doesGuiPauseGame() {
		return false;
	}

	/**
	 * Fired when a key is typed. This is the equivalent of
	 * KeyListener.keyTyped(KeyEvent e).
	 */
	protected void keyTyped(char par1, int par2) {
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		this.viewportTexture = this.mc.renderEngine
				.allocateAndSetupTexture(new BufferedImage(256, 256, 2));
		Calendar var1 = Calendar.getInstance();
		var1.setTime(new Date());

		if (var1.get(2) + 1 == 11 && var1.get(5) == 9) {
			this.splashText = "Happy birthday, ez!";
		} else if (var1.get(2) + 1 == 6 && var1.get(5) == 1) {
			this.splashText = "Happy birthday, Notch!";
		} else if (var1.get(2) + 1 == 12 && var1.get(5) == 24) {
			this.splashText = "Merry X-mas!";
		} else if (var1.get(2) + 1 == 1 && var1.get(5) == 1) {
			this.splashText = "Happy new year!";
		} else if (var1.get(2) + 1 == 10 && var1.get(5) == 31) {
			this.splashText = "OOoooOOOoooo! Spooky!";
		}

		StringTranslate var2 = StringTranslate.getInstance();
		int var4 = this.height / 4 + 48;

		if (this.mc.isDemo()) {
			this.addDemoButtons(var4, 24, var2);
		} else {
			this.addSingleplayerMultiplayerButtons(var4, 24, var2);
		}

		controlList.add(new GuiFancyButton(3, width, height - 115, var2
				.translateKey("menu.mods"), 2));

		Boolean ForgeInstalled = false;
		try {
			Class.forName("cpw.mods.fml.client.GuiModList");
			ForgeInstalled = true;
		} catch (ClassNotFoundException e) {
			ForgeInstalled = false;
		}



		if (ForgeInstalled) {
			controlList
					.add(new GuiFancyButton(8, width, height - 55, "Mods", 2));
		}

        controlList.add(new GuiFancyButton(100, 0, height - 55, "Friends", 1));

		controlList.add(new GuiFancyButton(0, width, height - 75, var2
				.translateKey("menu.options"), 2));
		controlList.add(new GuiFancyButton(4, width, height - 30, var2
				.translateKey("menu.quit"), 2));

		this.controlList.add(new GuiFancyButton(5, width, height - 95, var2
				.translateKey("options.language").replace("...", ""), 2));
		this.field_92025_p = "";
		String var5 = System.getProperty("os_architecture");
		String var6 = System.getProperty("java_version");

		if ("ppc".equalsIgnoreCase(var5)) {
			this.field_92025_p = "\u00a7lNotice!\u00a7r PowerPC compatibility will be dropped in Minecraft 1.6";
		} else if (var6 != null && var6.startsWith("1.5")) {
			this.field_92025_p = "\u00a7lNotice!\u00a7r Java 1.5 compatibility will be dropped in Minecraft 1.6";
		}

		this.field_92023_s = this.fontRenderer
				.getStringWidth(this.field_92025_p);
		this.field_92024_r = this.fontRenderer
				.getStringWidth("Please click \u00a7nhere\u00a7r for more information.");
		int var7 = Math.max(this.field_92023_s, this.field_92024_r);
		this.field_92022_t = (this.width - var7) / 2;
		this.field_92021_u = ((GuiButton) this.controlList.get(0)).yPosition - 24;
		this.field_92020_v = this.field_92022_t + var7;
		this.field_92019_w = this.field_92021_u + 24;

		bg = new GuiFancyRotatingBackground(mc, width, height, zLevel);
		saveList = SaveList.getSaveList(mc);
	}

	/**
	 * Adds Singleplayer and Multiplayer buttons on Main Menu for players who
	 * have bought the game.
	 */
	private void addSingleplayerMultiplayerButtons(int par1, int par2,
			StringTranslate par3StringTranslate) {
		controlList.add(new GuiFancyButton(1, 0, height - 120,
				par3StringTranslate.translateKey("menu.singleplayer"), 1));
		controlList.add(new GuiFancyButton(2, 0, height - 80,
				par3StringTranslate.translateKey("menu.multiplayer"), 1));
		controlList.add(new GuiFancyButton(6, 0, height - 140, "Continue", 1));
		controlList.add(new GuiFancyButton(9, 0, height - 100, "Connect", 1));
	}

	/**
	 * Adds Demo buttons on Main Menu for players who are playing Demo.
	 */
	private void addDemoButtons(int par1, int par2,
			StringTranslate par3StringTranslate) {
		this.controlList.add(new GuiFancyButton(11, 0, this.height - 120,
				par3StringTranslate.translateKey("menu.playdemo"), 1));
		this.controlList.add(buttonResetDemo = new GuiFancyButton(12, 0,
				height - 90,
				par3StringTranslate.translateKey("menu.resetdemo"), 1));
		ISaveFormat var4 = this.mc.getSaveLoader();
		WorldInfo var5 = var4.getWorldInfo("Demo_World");

		if (var5 == null) {
			this.buttonResetDemo.enabled = false;
		}
	}

	/**
	 * Fired when a control is clicked. This is the equivalent of
	 * ActionListener.actionPerformed(ActionEvent e).
	 */
	protected void actionPerformed(GuiButton par1GuiButton) {
		if (par1GuiButton.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (par1GuiButton.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings));
		}

		if (par1GuiButton.id == 1) {
			this.mc.displayGuiScreen(new GuiSelectWorld(this));
		}

		if (par1GuiButton.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}

		if (par1GuiButton.id == 3) {
			this.mc.displayGuiScreen(new GuiTexturePacks(this));
		}

		if (par1GuiButton.id == 4) {
			this.mc.shutdown();
		}

		if (par1GuiButton.id == 11) {
			this.mc.launchIntegratedServer("Demo_World", "Demo_World",
					DemoWorldServer.demoWorldSettings);
		}

		if (par1GuiButton.id == 12) {
			ISaveFormat var2 = this.mc.getSaveLoader();
			WorldInfo var3 = var2.getWorldInfo("Demo_World");

			if (var3 != null) {
				GuiYesNo var4 = GuiSelectWorld.getDeleteWorldScreen(this,
						var3.getWorldName(), 12);
				this.mc.displayGuiScreen(var4);
			}
		}

		if (par1GuiButton.id == 6) {
			if (saveList.isEmpty()) {
				return;
			}

			mc.displayGuiScreen(null);

			String s = ((SaveFormatComparator) saveList.get(0)).getFileName();

			if (s == null) {
				s = (new StringBuilder()).append("World").append(0).toString();
			}

			String s1 = ((SaveFormatComparator) saveList.get(0))
					.getDisplayName();

			if (s1 == null || MathHelper.stringNullOrLengthZero(s1)) {
				StringTranslate stringtranslate = StringTranslate.getInstance();
				s1 = (new StringBuilder())
						.append(stringtranslate
								.translateKey("selectWorld.world")).append(" ")
						.append(1).toString();
			}

			if (this.mc.getSaveLoader().canLoadWorld(s)) {
				this.mc.launchIntegratedServer(s, s1, (WorldSettings) null);
			}

			mc.displayGuiScreen(null);
			return;
		}

		if (par1GuiButton.id == 8) {
			Boolean ForgeInstalled = false;
			try {
				Class.forName("cpw.mods.fml.client.GuiModList");
				ForgeInstalled = true;
			} catch (ClassNotFoundException e) {
				ForgeInstalled = false;
			}

			if (ForgeInstalled) {
				try {
					Constructor c = Class.forName(
							"cpw.mods.fml.client.GuiModList").getConstructor(
							new Class[] { GuiScreen.class });
					GuiScreen gs = (GuiScreen) c
							.newInstance(new Object[] { this });
					this.mc.displayGuiScreen(gs);
				} catch (Exception e) {
				}
			}
			return;
		}

		if (par1GuiButton.id == 9) {
			if (mc.gameSettings.lastServer != "") {
				try {
					mc.displayGuiScreen(new GuiConnecting(this.mc,
							new ServerData("Last Server", URLDecoder.decode(
									mc.gameSettings.lastServer, "UTF-8"))));
				} catch (Exception e) {
				}
			}
			return;
		}

        if (par1GuiButton.id == 100) {
            Boolean FriendsOverlayInstalled = false;
            try {
                Class.forName("com.friendsoverlay.FriendsOverlay");
                FriendsOverlayInstalled = true;
            } catch (ClassNotFoundException e) {
                FriendsOverlayInstalled = false;
            }

            if (FriendsOverlayInstalled) {
                try {
                    Class c = Class.forName("com.friendsoverlay.FriendsOverlay");
                    Method m = c.getMethod("openMenu");
                    m.invoke(null);
                } catch (Exception e) {
                }
            } else {
                mc.displayGuiScreen(new GuiFancyNoFriendsOverlay(this));
            }
            return;
        }
	}

	public void confirmClicked(boolean par1, int par2) {
		if (par1 && par2 == 12) {
			ISaveFormat var6 = this.mc.getSaveLoader();
			var6.flushCache();
			var6.deleteWorldDirectory("Demo_World");
			this.mc.displayGuiScreen(this);
		} else if (par2 == 13) {
			if (par1) {
				try {
					Class var3 = Class.forName("java.awt.Desktop");
					Object var4 = var3.getMethod("getDesktop", new Class[0])
							.invoke((Object) null, new Object[0]);
					var3.getMethod("browse", new Class[] { URI.class })
							.invoke(var4,
									new Object[] { new URI(
											"http://tinyurl.com/javappc") });
				} catch (Throwable var5) {
					var5.printStackTrace();
				}
			}

			this.mc.displayGuiScreen(this);
		}
	}

	/**
	 * Draws the main menu panorama
	 */
	private void drawPanorama(int par1, int par2, float par3) {
		Tessellator var4 = Tessellator.instance;
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GLU.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glDepthMask(false);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		byte var5 = 8;

		for (int var6 = 0; var6 < var5 * var5; ++var6) {
			GL11.glPushMatrix();
			float var7 = ((float) (var6 % var5) / (float) var5 - 0.5F) / 64.0F;
			float var8 = ((float) (var6 / var5) / (float) var5 - 0.5F) / 64.0F;
			float var9 = 0.0F;
			GL11.glTranslatef(var7, var8, var9);
			GL11.glRotatef(
					MathHelper
							.sin(((float) this.panoramaTimer + par3) / 400.0F) * 25.0F + 20.0F,
					1.0F, 0.0F, 0.0F);
			GL11.glRotatef(-((float) this.panoramaTimer + par3) * 0.1F, 0.0F,
					1.0F, 0.0F);

			for (int var10 = 0; var10 < 6; ++var10) {
				GL11.glPushMatrix();

				if (var10 == 1) {
					GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (var10 == 2) {
					GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
				}

				if (var10 == 3) {
					GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
				}

				if (var10 == 4) {
					GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
				}

				if (var10 == 5) {
					GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
				}

				GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.mc.renderEngine
						.getTexture(titlePanoramaPaths[var10]));
				var4.startDrawingQuads();
				var4.setColorRGBA_I(16777215, 255 / (var6 + 1));
				float var11 = 0.0F;
				var4.addVertexWithUV(-1.0D, -1.0D, 1.0D,
						(double) (0.0F + var11), (double) (0.0F + var11));
				var4.addVertexWithUV(1.0D, -1.0D, 1.0D,
						(double) (1.0F - var11), (double) (0.0F + var11));
				var4.addVertexWithUV(1.0D, 1.0D, 1.0D, (double) (1.0F - var11),
						(double) (1.0F - var11));
				var4.addVertexWithUV(-1.0D, 1.0D, 1.0D,
						(double) (0.0F + var11), (double) (1.0F - var11));
				var4.draw();
				GL11.glPopMatrix();
			}

			GL11.glPopMatrix();
			GL11.glColorMask(true, true, true, false);
		}

		var4.setTranslation(0.0D, 0.0D, 0.0D);
		GL11.glColorMask(true, true, true, true);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glPopMatrix();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glPopMatrix();
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	/**
	 * Rotate and blurs the skybox view in the main menu
	 */
	private void rotateAndBlurSkybox(float par1) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.viewportTexture);
		GL11.glCopyTexSubImage2D(GL11.GL_TEXTURE_2D, 0, 0, 0, 0, 0, 256, 256);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColorMask(true, true, true, false);
		Tessellator var2 = Tessellator.instance;
		var2.startDrawingQuads();
		byte var3 = 3;

		for (int var4 = 0; var4 < var3; ++var4) {
			var2.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (float) (var4 + 1));
			int var5 = this.width;
			int var6 = this.height;
			float var7 = (float) (var4 - var3 / 2) / 256.0F;
			var2.addVertexWithUV((double) var5, (double) var6,
					(double) this.zLevel, (double) (0.0F + var7), 0.0D);
			var2.addVertexWithUV((double) var5, 0.0D, (double) this.zLevel,
					(double) (1.0F + var7), 0.0D);
			var2.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel,
					(double) (1.0F + var7), 1.0D);
			var2.addVertexWithUV(0.0D, (double) var6, (double) this.zLevel,
					(double) (0.0F + var7), 1.0D);
		}

		var2.draw();
		GL11.glColorMask(true, true, true, true);
	}

	/**
	 * Renders the skybox in the main menu
	 */
	private void renderSkybox(int par1, int par2, float par3) {
		GL11.glViewport(0, 0, 256, 256);
		this.drawPanorama(par1, par2, par3);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		this.rotateAndBlurSkybox(par3);
		GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		Tessellator var4 = Tessellator.instance;
		var4.startDrawingQuads();
		float var5 = this.width > this.height ? 120.0F / (float) this.width
				: 120.0F / (float) this.height;
		float var6 = (float) this.height * var5 / 256.0F;
		float var7 = (float) this.width * var5 / 256.0F;
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				GL11.GL_LINEAR);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				GL11.GL_LINEAR);
		var4.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
		int var8 = this.width;
		int var9 = this.height;
		var4.addVertexWithUV(0.0D, (double) var9, (double) this.zLevel,
				(double) (0.5F - var6), (double) (0.5F + var7));
		var4.addVertexWithUV((double) var8, (double) var9,
				(double) this.zLevel, (double) (0.5F - var6),
				(double) (0.5F - var7));
		var4.addVertexWithUV((double) var8, 0.0D, (double) this.zLevel,
				(double) (0.5F + var6), (double) (0.5F - var7));
		var4.addVertexWithUV(0.0D, 0.0D, (double) this.zLevel,
				(double) (0.5F + var6), (double) (0.5F + var7));
		var4.draw();
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int par1, int par2, float par3) {
		//TODO: Add option
		//fontRenderer.setUnicodeFlag(true);
		bg.RenderBackground(par1, par2, par3);
		Tessellator var4 = Tessellator.instance;
		short var5 = 274;
		int var6 = this.width / 2 - var5 / 2;
		byte var7 = 30;
		// this.drawGradientRect(0, 0, this.width, this.height, -2130706433,
		// 16777215);
		// this.drawGradientRect(0, 0, this.width, this.height, 0,
		// Integer.MIN_VALUE);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,
				this.mc.renderEngine.getTexture("/title/mclogo.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		this.drawTexturedModalRect(var6 + 0, var7 + 0, 0, 0, 155, 44);
		this.drawTexturedModalRect(var6 + 155, var7 + 0, 0, 45, 155, 44);

		var4.setColorOpaque_I(16777215);
		GL11.glPushMatrix();
		GL11.glTranslatef((float) (this.width / 2 + 90), 70.0F, 0.0F);
		GL11.glRotatef(-20.0F, 0.0F, 0.0F, 1.0F);
		float var8 = 1.8F - MathHelper
				.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L)
						/ 1000.0F * (float) Math.PI * 2.0F) * 0.1F);
		var8 = var8
				* 100.0F
				/ (float) (this.fontRenderer.getStringWidth(this.splashText) + 32);
		GL11.glScalef(var8, var8, var8);
		this.drawCenteredString(this.fontRenderer, this.splashText, 0, -8,
				16776960);
		GL11.glPopMatrix();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glPushMatrix();

		GL11.glScalef(0.8F, 0.8F, 0.8F);

		if (mc.gameSettings.lastServer != "") {
			try {
				drawString(
						fontRenderer,
						URLDecoder.decode(mc.gameSettings.lastServer, "UTF-8"),
						71,
						(int) (((double) height - 92.5D) / 0.80000000000000004D),
						0x32e0e0e0);
			} catch (Exception e) {
			}
		}

		if (!saveList.isEmpty()) {
			drawString(fontRenderer,
					((SaveFormatComparator) saveList.get(0)).getDisplayName(),
					71,
					(int) (((double) height - 132.5D) / 0.80000000000000004D),
					0x32e0e0e0);
		}

		GL11.glPopMatrix();
		String var9 = "v1.4.7";

		if (this.mc.isDemo()) {
			var9 = var9 + " Demo";
		}

		drawString(fontRenderer, var9, 2, 2, 0x64ffffff);

		GL11.glBindTexture(GL11.GL_TEXTURE_2D,
				mc.renderEngine.getTexture("/title/mojangt.png"));
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
		drawTexturedModalRect(2, height - 19 - 2, 0, 0, 128, 19);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		if (this.field_92025_p != null && this.field_92025_p.length() > 0) {
			drawRect(this.field_92022_t - 2, this.field_92021_u - 2,
					this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
			this.drawString(this.fontRenderer, this.field_92025_p,
					this.field_92022_t, this.field_92021_u, 16777215);
			this.drawString(this.fontRenderer,
					"Please click \u00a7nhere\u00a7r for more information.",
					(this.width - this.field_92024_r) / 2,
					((GuiButton) this.controlList.get(0)).yPosition - 12,
					16777215);
		}

		super.drawScreen(par1, par2, par3);
	}

	/**
	 * Called when the mouse is clicked.
	 */
	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);

		if (this.field_92025_p.length() > 0 && par1 >= this.field_92022_t
				&& par1 <= this.field_92020_v && par2 >= this.field_92021_u
				&& par2 <= this.field_92019_w) {
			GuiConfirmOpenLink var4 = new GuiConfirmOpenLink(this,
					"http://tinyurl.com/javappc", 13);
			var4.func_92026_h();
			this.mc.displayGuiScreen(var4);
		}
	}
}