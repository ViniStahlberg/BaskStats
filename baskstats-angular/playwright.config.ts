import { defineConfig } from "@playwright/test";
export default defineConfig({
  testDir: "./e2e",
  fullyParallel: true,
  use: {
    baseURL: "http://127.0.0.1:4200",
    viewport: { width: 1440, height: 900 },
    trace: "retain-on-failure",
    launchOptions: process.env["PLAYWRIGHT_CHROMIUM_EXECUTABLE"]
      ? { executablePath: process.env["PLAYWRIGHT_CHROMIUM_EXECUTABLE"] }
      : {},
  },
  webServer: {
    command: "npm start -- --host 127.0.0.1",
    url: "http://127.0.0.1:4200",
    reuseExistingServer: !process.env["CI"],
  },
});
