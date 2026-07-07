using UnityEditor;
using UnityEngine;
using UnityEngine.Rendering;

public static class SceneSetup
{
    [MenuItem("Tools/Setup Game Scene")]
    public static void Setup()
    {
        Cleanup("Player");
        Cleanup("Ground");
        Cleanup("Roads");
        Cleanup("Buildings");
        Cleanup("Cars");
        Cleanup("TrafficLights");
        Cleanup("Skyscraper");
        Cleanup("Trees");
        Cleanup("Lamps");
        Cleanup("Monster");
        Cleanup("BigRoads");
        Cleanup("BigBuildings");
        Cleanup("BigLamps");
        Cleanup("BigCars");
        Cleanup("BigHighway");
        Cleanup("BigSkyscraper");
        Cleanup("BigCityGround");
        Cleanup("BigTrees");
        var oldCams = Object.FindObjectsOfType<Camera>();

        // === Ground (200x200) ===
        var ground = Make(PrimitiveType.Plane, "Ground", Vector3.zero, Vector3.one * 20);
        Paint(ground, new Color(0.003f, 0.002f, 0.001f));

        // === Roads (5x5 grid) ===
        var roadMat = new Material(Shader.Find("Standard")) { color = new Color(0.005f, 0.005f, 0.005f) };
        roadMat.SetFloat("_Glossiness", 0f);
        roadMat.SetFloat("_Metallic", 0f);
        var roads = new GameObject("Roads").transform;
        float[] rg = { -60f, -30f, 0f, 30f, 60f };
        float roadLen = 140f;
        foreach (float z in rg)
        {
            var r = Make(PrimitiveType.Cube, null, new Vector3(0, 0.02f, z), new Vector3(roadLen, 0.04f, 4));
            r.GetComponent<MeshRenderer>().material = roadMat;
            r.transform.parent = roads;
        }
        foreach (float x in rg)
        {
            var r = Make(PrimitiveType.Cube, null, new Vector3(x, 0.02f, 0), new Vector3(4, 0.04f, roadLen));
            r.GetComponent<MeshRenderer>().material = roadMat;
            r.transform.parent = roads;
        }

        // === Skyscraper (center, 100m, enterable) ===
        var sp = new GameObject("Skyscraper").transform;
        Tower(Vector3.zero, sp);
        var plaza = Make(PrimitiveType.Cube, null, new Vector3(0, 0.01f, 16), new Vector3(18, 0.02f, 10));
        Paint(plaza, new Color(0.003f, 0.003f, 0.003f));
        plaza.transform.parent = sp;

        // === City blocks: 4x4 interior blocks + edge blocks ===
        var bp = new GameObject("Buildings").transform;
        Color[] hc = {
            new Color(0.68f, 0.7f, 0.76f),  // 白墙→冷瓷白
            new Color(0.28f, 0.1f, 0.12f),  // 红砖→深紫褐
            new Color(0.2f, 0.19f, 0.24f),  // 水泥→铅灰带青
            new Color(0.19f, 0.13f, 0.07f), // 木质→暖灰褐
            new Color(0.07f, 0.09f, 0.07f), // 深色石材→墨青
            new Color(0.23f, 0.17f, 0.14f), // 混合砖木
            new Color(0.14f, 0.13f, 0.16f), // 铁皮→暗银灰
            new Color(0.11f, 0.13f, 0.09f), // 铜锈→灰绿
        };
        Color[] oc = {
            new Color(0.035f, 0.035f, 0.09f), // 玻璃幕墙→深蓝黑
            new Color(0.19f, 0.18f, 0.21f),   // 混凝土→铅灰
            new Color(0.12f, 0.12f, 0.15f),   // 金属→暗银灰
            new Color(0.06f, 0.07f, 0.06f),   // 深色石材→墨青
        };
        // Interior blocks (between -60 and 60, centered at ±45, ±15)
        float[] bc = { -45f, -15f, 15f, 45f };
        int ci = 0, oi = 0;
        foreach (float bx in bc)
        {
            foreach (float bz in bc)
            {
                // Skip center block (occupied by skyscraper)
                if (Mathf.Abs(bx) < 10f && Mathf.Abs(bz) < 10f) continue;
                // Place 1 house + 1 office per block
                House(new Vector3(bx + 5, 0, bz + 5), hc[ci % hc.Length], bp);
                Office(new Vector3(bx - 5, 0, bz - 5), oc[oi % oc.Length], bp);
                ci++; oi++;
            }
        }
        // Edge blocks (outside the grid)
        float[] outerZ = { -75f, 75f };
        foreach (float oz in outerZ)
        {
            foreach (float bx in bc)
            {
                House(new Vector3(bx + 5, 0, oz + 5), hc[ci % hc.Length], bp);
                House(new Vector3(bx - 5, 0, oz - 5), hc[(ci + 1) % hc.Length], bp);
                ci += 2;
            }
        }
        float[] outerX = { -75f, 75f };
        foreach (float ox in outerX)
        {
            foreach (float bz in bc)
            {
                House(new Vector3(ox + 5, 0, bz + 5), hc[ci % hc.Length], bp);
                House(new Vector3(ox - 5, 0, bz - 5), hc[(ci + 1) % hc.Length], bp);
                ci += 2;
            }
        }

        // === Trees ===
        var tpTree = new GameObject("Trees").transform;
        foreach (float bx in bc)
        {
            foreach (float bz in bc)
            {
                if (Mathf.Abs(bx) < 10f && Mathf.Abs(bz) < 10f) continue;
                Tree(new Vector3(bx + 6, 0, bz - 6), tpTree);
                Tree(new Vector3(bx - 6, 0, bz + 6), tpTree);
            }
        }

        // === Cars ===
        var cp = new GameObject("Cars").transform;
        Car(new Vector3(5, 0, 0.5f), Color.red, cp);
        Car(new Vector3(-5, 0, -0.5f), Color.blue, cp);
        Car(new Vector3(0.5f, 0, 5), Color.green, cp);
        Car(new Vector3(-0.5f, 0, -5), Color.yellow, cp);
        Car(new Vector3(10, 0, 0), Color.cyan, cp);
        Car(new Vector3(-10, 0, 0), Color.magenta, cp);
        Car(new Vector3(0, 0, 10), Color.white, cp);
        Car(new Vector3(0, 0, -10), new Color(1, 0.5f, 0), cp);
        Car(new Vector3(15, 0, 0.5f), new Color(0.5f, 0.8f, 0.2f), cp);
        Car(new Vector3(-15, 0, -0.5f), new Color(0.8f, 0.2f, 0.5f), cp);

        // === Traffic Lights ===
        var tlP = new GameObject("TrafficLights").transform;
        float[] tlp = { -60f, -30f, 30f, 60f };
        foreach (float tx in tlp)
        {
            TL(new Vector3(tx, 0, 0.5f), tlP);
            TL(new Vector3(tx, 0, -0.5f), tlP);
        }
        foreach (float tz in tlp)
        {
            TL(new Vector3(0.5f, 0, tz), tlP);
            TL(new Vector3(-0.5f, 0, tz), tlP);
        }

        // === Monster ===
        var mp = new GameObject("Monster").transform;
        BlackMonster(new Vector3(28, 0, 38), mp);

        // === Player ===
        var player = GameObject.CreatePrimitive(PrimitiveType.Capsule);
        player.name = "Player";
        player.tag = "Player";
        player.transform.position = new Vector3(28, 1.5f, 28);
        var cols = player.GetComponents<Collider>();
        foreach (var c in cols) Object.DestroyImmediate(c);
        var pc = player.AddComponent<CapsuleCollider>();
        pc.height = 2;
        var pm = new PhysicMaterial("ZeroFriction");
        pm.dynamicFriction = 0f;
        pm.staticFriction = 0f;
        pm.frictionCombine = PhysicMaterialCombine.Minimum;
        pc.material = pm;

        var rb = player.AddComponent<Rigidbody>();
        rb.constraints = RigidbodyConstraints.FreezeRotation;
        rb.mass = 1;
        player.AddComponent<PlayerController>();

        // === Camera ===
        var camGO = new GameObject("MainCamera");
        camGO.transform.SetParent(player.transform);
        camGO.transform.localPosition = new Vector3(0, 0.6f, 0);
        var camComp = camGO.AddComponent<Camera>();
        camGO.tag = "MainCamera";
        foreach (var c in oldCams)
            if (c.gameObject != camGO && c.transform.parent == null)
                Object.DestroyImmediate(c.gameObject);

        AttachGun(camGO.transform);

        // === Day Lighting ===
        RenderSettings.ambientMode = AmbientMode.Skybox;
        RenderSettings.ambientLight = new Color(0.5f, 0.5f, 0.5f);
        var skyMat = new Material(Shader.Find("Skybox/Procedural"));
        skyMat.SetFloat("_SunSize", 0.04f);
        skyMat.SetColor("_SkyTint", new Color(0.5f, 0.6f, 1f));
        skyMat.SetFloat("_Exposure", 1f);
        skyMat.SetFloat("_AtmosphereThickness", 1f);
        RenderSettings.skybox = skyMat;
        RenderSettings.fog = false;

        camComp.clearFlags = CameraClearFlags.Skybox;

        var lig = new GameObject("Directional Light");
        lig.transform.position = new Vector3(0, 60, 0);
        lig.transform.eulerAngles = new Vector3(50, -30, 0);
        var lt = lig.AddComponent<Light>();
        lt.type = LightType.Directional;
        lt.color = Color.white;
        lt.intensity = 1.2f;
        lt.shadowStrength = 0.8f;

        // === Street Lamps ===
        var lp = new GameObject("Lamps").transform;
        float[] lpPos = { -60f, -30f, 30f, 60f };
        foreach (float px in lpPos)
        {
            Lamp(new Vector3(px, 0, 2.5f), lp);
            Lamp(new Vector3(px, 0, -2.5f), lp);
        }
        foreach (float pz in lpPos)
        {
            Lamp(new Vector3(2.5f, 0, pz), lp);
            Lamp(new Vector3(-2.5f, 0, pz), lp);
        }

        // === 新大城市 (280, 0, 0) ===
        BuildBigCity(new Vector3(280, 0, 0));

        Selection.activeGameObject = player;
        SceneView.FrameLastActiveSceneView();
        Debug.Log("夜晚双城已建成");
    }

    static void BuildBigCity(Vector3 c)
    {
        float ox = c.x, oz = c.z;

        var ground = Make(PrimitiveType.Plane, "BigCityGround", c, Vector3.one * 30);
        Paint(ground, new Color(0.003f, 0.002f, 0.001f));

        // Trees (more than old city)
        var tpTree = new GameObject("BigTrees").transform;
        float[] tGrid = { -60f, -30f, 0f, 30f, 60f, 90f };
        foreach (float bx in tGrid) foreach (float bz in tGrid)
        {
            if (Mathf.Abs(bx) < 15f && Mathf.Abs(bz) < 15f) continue;
            Tree(new Vector3(ox + bx + Random.Range(-10f, 10f), 0, oz + bz + Random.Range(-10f, 10f)), tpTree);
        }

        var roads = new GameObject("BigRoads").transform;
        var bp = new GameObject("BigBuildings").transform;
        var lp = new GameObject("BigLamps").transform;
        var cp = new GameObject("BigCars").transform;

        var roadMat = new Material(Shader.Find("Standard")) { color = new Color(0.008f, 0.008f, 0.008f) };
        roadMat.SetFloat("_Glossiness", 0f);
        float[] rg = { -80f, -50f, -20f, 10f, 40f, 70f, 100f };
        float roadLen = 200f;
        foreach (float z in rg)
        {
            var r = Make(PrimitiveType.Cube, null, new Vector3(ox, 0.02f, oz + z), new Vector3(roadLen, 0.04f, 5));
            r.GetComponent<MeshRenderer>().material = roadMat;
            r.transform.parent = roads;
        }
        foreach (float x in rg)
        {
            var r = Make(PrimitiveType.Cube, null, new Vector3(ox + x, 0.02f, oz), new Vector3(5, 0.04f, roadLen));
            r.GetComponent<MeshRenderer>().material = roadMat;
            r.transform.parent = roads;
        }

        // Highway ring (elevated loop)
        var hwMat = new Material(Shader.Find("Standard")) { color = new Color(0.015f, 0.015f, 0.015f) };
        var hwP = new GameObject("BigHighway").transform;
        float[] hwPx = { -120f, 120f }; float[] hwPz = { -120f, 120f };
        foreach (float px in hwPx) for (float pz = -120f; pz <= 120f; pz += 2f) { var s = Make(PrimitiveType.Cube, null, new Vector3(ox + px, 5.0f, oz + pz), new Vector3(8, 0.2f, 2)); s.GetComponent<MeshRenderer>().material = hwMat; s.transform.parent = hwP; }
        foreach (float pz in hwPz) for (float px = -120f; px <= 120f; px += 2f) { var s = Make(PrimitiveType.Cube, null, new Vector3(ox + px, 5.0f, oz + pz), new Vector3(2, 0.2f, 8)); s.GetComponent<MeshRenderer>().material = hwMat; s.transform.parent = hwP; }
        // Highway pillars
        var pillarMat = new Material(Shader.Find("Standard")) { color = new Color(0.02f, 0.02f, 0.02f) };
        float[] hp = { -120f, 120f };
        foreach (float px in hp) foreach (float pz in hp) {
            var p = Make(PrimitiveType.Cylinder, null, new Vector3(ox + px, 2.5f, oz + pz), new Vector3(0.3f, 5, 0.3f));
            p.GetComponent<MeshRenderer>().material = pillarMat; p.transform.parent = hwP;
        }
        foreach (float px in hp) { var p = Make(PrimitiveType.Cylinder, null, new Vector3(ox + px, 2.5f, oz + 0), new Vector3(0.3f, 5, 0.3f)); p.GetComponent<MeshRenderer>().material = pillarMat; p.transform.parent = hwP; }
        foreach (float pz in hp) { var p = Make(PrimitiveType.Cylinder, null, new Vector3(ox + 0, 2.5f, oz + pz), new Vector3(0.3f, 5, 0.3f)); p.GetComponent<MeshRenderer>().material = pillarMat; p.transform.parent = hwP; }

        // Buildings — dense grid, 10 blocks x 10 blocks
        Color[] cols = {
            new Color(0.5f, 0.55f, 0.65f), new Color(0.08f, 0.1f, 0.14f), new Color(0.2f, 0.25f, 0.3f),
            new Color(0.15f, 0.05f, 0.05f), new Color(0.1f, 0.15f, 0.1f), new Color(0.35f, 0.3f, 0.25f),
            new Color(0.02f, 0.06f, 0.1f), new Color(0.25f, 0.2f, 0.15f), new Color(0.12f, 0.12f, 0.18f),
            new Color(0.08f, 0.12f, 0.08f), new Color(0.4f, 0.4f, 0.45f), new Color(0.05f, 0.03f, 0.06f),
        };
        float[] bGrid = { -65f, -35f, -5f, 25f, 55f, 85f };
        int ci = 0;
        foreach (float bx in bGrid) foreach (float bz in bGrid)
        {
            bool center = Mathf.Abs(bx) < 20f && Mathf.Abs(bz) < 20f;
            int bldgs = center ? 0 : Random.Range(2, 5);
            for (int b = 0; b < bldgs; b++)
            {
                Vector3 pos = new Vector3(ox + bx + Random.Range(-8f, 8f), 0, oz + bz + Random.Range(-8f, 8f));
                Color col = cols[(ci++) % cols.Length];
                float h = center ? Random.Range(30f, 60f) : (Random.Range(0, 3) == 0 ? Random.Range(15f, 40f) : Random.Range(4f, 12f));
                float w = h > 15f ? Random.Range(5f, 8f) : Random.Range(3f, 6f);
                float d = h > 15f ? Random.Range(5f, 8f) : Random.Range(3f, 6f);
                RoomBuilding(pos, w, d, h, col, bp);
            }
        }

        // Central skyscraper (150m)
        var sp = new GameObject("BigSkyscraper").transform;
        Tower(new Vector3(ox, 0, oz), sp);
        // Second tower nearby
        Tower2(new Vector3(ox + 25, 0, oz + 20), sp);

        // Neon billboards
        var bMat = new Material(Shader.Find("Standard"));
        bMat.EnableKeyword("_EMISSION");
        bMat.color = new Color(0.8f, 0.2f, 0.8f);
        bMat.SetColor("_EmissionColor", new Color(3f, 0.5f, 3f));
        var bb1 = Make(PrimitiveType.Cube, null, new Vector3(ox - 95, 8, oz - 80), new Vector3(0.1f, 10, 6));
        bb1.GetComponent<MeshRenderer>().material = bMat; bb1.transform.parent = bp;

        var bbMat2 = new Material(Shader.Find("Standard"));
        bbMat2.EnableKeyword("_EMISSION");
        bbMat2.color = new Color(0.2f, 0.8f, 1f);
        bbMat2.SetColor("_EmissionColor", new Color(0.5f, 3f, 4f));
        var bb2 = Make(PrimitiveType.Cube, null, new Vector3(ox + 95, 8, oz + 80), new Vector3(0.1f, 10, 6));
        bb2.GetComponent<MeshRenderer>().material = bbMat2; bb2.transform.parent = bp;
        // Billboard posts
        for (int i = -1; i <= 1; i += 2) for (int j = -1; j <= 1; j += 2) {
            var post = Make(PrimitiveType.Cylinder, null, new Vector3(ox + i * 95, 3, oz + j * 80), new Vector3(0.08f, 6, 0.08f));
            Paint(post, Color.gray); post.transform.parent = bp;
        }

        // Street lamps (more frequent)
        foreach (float x in new float[]{ -80f, -50f, -20f, 10f, 40f, 70f, 100f })
        {
            Lamp(new Vector3(ox + x, 0, oz + 3), lp);
            Lamp(new Vector3(ox + x, 0, oz - 3), lp);
        }
        foreach (float z in new float[]{ -80f, -50f, -20f, 10f, 40f, 70f, 100f })
        {
            Lamp(new Vector3(ox + 3, 0, oz + z), lp);
            Lamp(new Vector3(ox - 3, 0, oz + z), lp);
        }

        // Cars — many scattered on roads
        Color[] carCols = { Color.red, Color.blue, Color.green, Color.yellow, Color.cyan, Color.magenta, Color.white, new Color(1,0.5f,0), new Color(0.5f,0.8f,0.2f), new Color(0.8f,0.2f,0.5f), new Color(0.2f,0.6f,0.8f), new Color(0.6f,0.3f,0.1f) };
        float[] cr = { -80f, -50f, -20f, 10f, 40f, 70f, 100f };
        for (int i = 0; i < 24; i++)
        {
            bool axisX = Random.Range(0, 2) == 0;
            float r1 = axisX ? cr[Random.Range(0, cr.Length)] : Random.Range(-90f, 90f);
            float r2 = axisX ? Random.Range(-90f, 90f) : cr[Random.Range(0, cr.Length)];
            Vector3 carPos = new Vector3(ox + r1 + (axisX ? 0 : Random.Range(-1.5f, 1.5f)), 0, oz + r2 + (axisX ? Random.Range(-1.5f, 1.5f) : 0));
            Car(carPos, carCols[i % carCols.Length], cp);
        }
    }

    static void RoomBuilding(Vector3 pos, float w, float d, float h, Color c, Transform p)
    {
        float th = 0.15f;
        var wallMat = new Material(Shader.Find("Standard")) { color = c };
        wallMat.EnableKeyword("_EMISSION");
        wallMat.SetColor("_EmissionColor", new Color(0.03f, 0.03f, 0.04f));
        // Back wall
        var bw = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h / 2, -d / 2 + th / 2), new Vector3(w, h, th));
        bw.GetComponent<MeshRenderer>().material = wallMat; bw.transform.parent = p;
        // Left wall
        var lw = Make(PrimitiveType.Cube, null, pos + new Vector3(-w / 2 + th / 2, h / 2, 0), new Vector3(th, h, d));
        lw.GetComponent<MeshRenderer>().material = wallMat; lw.transform.parent = p;
        // Right wall
        var rw = Make(PrimitiveType.Cube, null, pos + new Vector3(w / 2 - th / 2, h / 2, 0), new Vector3(th, h, d));
        rw.GetComponent<MeshRenderer>().material = wallMat; rw.transform.parent = p;
        // Front wall with door gap
        float doorW = Mathf.Min(1.2f, Mathf.Max(1f, w * 0.3f));
        float fw = Mathf.Max(0, (w - doorW - th) / 2);
        if (fw > 0)
        {
            var fwl = Make(PrimitiveType.Cube, null, pos + new Vector3(-doorW / 2 - fw / 2 - th / 2, h / 2, d / 2 - th / 2), new Vector3(fw, h, th));
            fwl.GetComponent<MeshRenderer>().material = wallMat; fwl.transform.parent = p;
            var fwr = Make(PrimitiveType.Cube, null, pos + new Vector3(doorW / 2 + fw / 2 + th / 2, h / 2, d / 2 - th / 2), new Vector3(fw, h, th));
            fwr.GetComponent<MeshRenderer>().material = wallMat; fwr.transform.parent = p;
        }
        else
        {
            var fwF = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h / 2, d / 2 - th / 2), new Vector3(doorW, h, th));
            fwF.GetComponent<MeshRenderer>().material = wallMat; fwF.transform.parent = p;
        }
        // Floor slabs (every 3m, skip ground floor)
        var floorMat = new Material(Shader.Find("Standard")) { color = new Color(0.02f, 0.02f, 0.02f) };
        floorMat.EnableKeyword("_EMISSION");
        floorMat.SetColor("_EmissionColor", new Color(0.035f, 0.035f, 0.04f));
        float floorH = 3f;
        int numFloors = Mathf.FloorToInt((h - 0.5f) / floorH);
        for (int f = 1; f < numFloors; f++)
        {
            float y = f * floorH;
            var slab = Make(PrimitiveType.Cube, null, pos + new Vector3(0, y, 0), new Vector3(w - th * 2, 0.08f, d - th * 2));
            slab.GetComponent<MeshRenderer>().material = floorMat;
            slab.transform.parent = p;
        }
        // Roof
        var roof = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h + 0.05f, 0), new Vector3(w + 0.15f, 0.08f, d + 0.15f));
        Paint(roof, c * 0.6f);
        roof.transform.parent = p;
    }

    static void Tower2(Vector3 pos, Transform p)
    {
        float w = 18, d = 18, h = 80, th = 0.4f;
        var wallMat = new Material(Shader.Find("Standard")) { color = new Color(0.05f, 0.06f, 0.1f) };
        wallMat.EnableKeyword("_EMISSION");
        wallMat.SetColor("_EmissionColor", new Color(0.04f, 0.04f, 0.05f));
        var bw = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h / 2, -d / 2 + th / 2), new Vector3(w, h, th));
        bw.GetComponent<MeshRenderer>().material = wallMat; bw.transform.parent = p;
        var lw = Make(PrimitiveType.Cube, null, pos + new Vector3(-w / 2 + th / 2, h / 2, 0), new Vector3(th, h, d));
        lw.GetComponent<MeshRenderer>().material = wallMat; lw.transform.parent = p;
        var rw = Make(PrimitiveType.Cube, null, pos + new Vector3(w / 2 - th / 2, h / 2, 0), new Vector3(th, h, d));
        rw.GetComponent<MeshRenderer>().material = wallMat; rw.transform.parent = p;
        // Window bands
        var bandMat = new Material(Shader.Find("Standard")) { color = new Color(0.2f, 0.2f, 0.25f) };
        bandMat.SetFloat("_Glossiness", 0.9f);
        bandMat.EnableKeyword("_EMISSION");
        bandMat.SetColor("_EmissionColor", new Color(0.04f, 0.04f, 0.05f));
        for (int i = 0; i < 16; i++) { float y = (i + 1) * 5f; var ba = Make(PrimitiveType.Cube, null, pos + new Vector3(0, y, 0), new Vector3(w - 0.3f, 0.08f, d - 0.3f)); ba.GetComponent<MeshRenderer>().material = bandMat; ba.transform.parent = p; }
        var roof = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h + 0.5f, 0), new Vector3(w + 1, 1, d + 1));
        roof.GetComponent<MeshRenderer>().material = wallMat; roof.transform.parent = p;
        var spire = Make(PrimitiveType.Cylinder, null, pos + new Vector3(0, h + 5, 0), new Vector3(0.4f, 8, 0.4f));
        Paint(spire, Color.gray); spire.transform.parent = p;
    }

    static void Cleanup(string n)
    {
        var g = GameObject.Find(n);
        if (g != null) Object.DestroyImmediate(g);
    }

    static GameObject Make(PrimitiveType t, string n, Vector3 p, Vector3 s)
    {
        var g = GameObject.CreatePrimitive(t);
        g.name = n ?? t.ToString();
        g.transform.position = p;
        g.transform.localScale = s;
        return g;
    }

    static void Paint(GameObject g, Color c)
    {
        var m = new Material(Shader.Find("Standard")) { color = c };
        g.GetComponent<MeshRenderer>().material = m;
    }

    static Material BuildMat(Color c)
    {
        var m = new Material(Shader.Find("Standard")) { color = c };
        m.EnableKeyword("_EMISSION");
        m.SetColor("_EmissionColor", new Color(0.04f, 0.04f, 0.05f));
        return m;
    }
    static void PaintBuild(GameObject g, Color c)
    {
        g.GetComponent<MeshRenderer>().material = BuildMat(c);
    }

    static void House(Vector3 pos, Color c, Transform p)
    {
        float w = 4, d = 4, h = 3, th = 0.2f;
        var fl = Make(PrimitiveType.Cube, null, pos + new Vector3(0, th / 2, 0), new Vector3(w, th, d));
        Paint(fl, new Color(0.02f, 0.02f, 0.02f));
        fl.transform.parent = p;
        var bw = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h / 2, -d / 2 + th / 2), new Vector3(w, h, th));
        PaintBuild(bw, c);
        bw.transform.parent = p;
        var lw = Make(PrimitiveType.Cube, null, pos + new Vector3(-w / 2 + th / 2, h / 2, 0), new Vector3(th, h, d));
        PaintBuild(lw, c);
        lw.transform.parent = p;
        var rw = Make(PrimitiveType.Cube, null, pos + new Vector3(w / 2 - th / 2, h / 2, 0), new Vector3(th, h, d));
        PaintBuild(rw, c);
        rw.transform.parent = p;
        float door = 1f, fw = (w - door - th) / 2;
        var fwl = Make(PrimitiveType.Cube, null, pos + new Vector3(-fw / 2 - door / 2 - th / 2, h / 2, d / 2 - th / 2), new Vector3(fw, h, th));
        PaintBuild(fwl, c);
        fwl.transform.parent = p;
        var fwr = Make(PrimitiveType.Cube, null, pos + new Vector3(fw / 2 + door / 2 + th / 2, h / 2, d / 2 - th / 2), new Vector3(fw, h, th));
        PaintBuild(fwr, c);
        fwr.transform.parent = p;
        var roof = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h + th / 2, 0), new Vector3(w + 0.4f, th, d + 0.4f));
        Paint(roof, c * 0.6f);
        roof.transform.parent = p;
    }

    static void Office(Vector3 pos, Color c, Transform p)
    {
        float w = 6, d = 6, h = 10;
        var b = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h / 2, 0), new Vector3(w, h, d));
        PaintBuild(b, c);
        b.transform.parent = p;
        var bands = BuildMat(c * 1.3f);
        for (int i = 0; i < 5; i++)
        {
            var ba = Make(PrimitiveType.Cube, null, pos + new Vector3(0, (i + 0.5f) * 2f, 0), new Vector3(w + 0.2f, 0.1f, d + 0.2f));
            ba.GetComponent<MeshRenderer>().material = bands;
            ba.transform.parent = p;
        }
        var roof = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h + 0.2f, 0), new Vector3(w + 0.3f, 0.4f, d + 0.3f));
        Paint(roof, c * 0.6f);
        roof.transform.parent = p;
    }

    static void Tree(Vector3 pos, Transform p)
    {
        var trunk = Make(PrimitiveType.Cylinder, null, pos + new Vector3(0, 1f, 0), new Vector3(0.08f, 2, 0.08f));
        Paint(trunk, new Color(0.4f, 0.25f, 0.1f));
        trunk.transform.parent = p;
        var crown = Make(PrimitiveType.Sphere, null, pos + new Vector3(0, 2.5f, 0), new Vector3(1.2f, 1.2f, 1.2f));
        Paint(crown, new Color(0.15f, 0.55f, 0.1f));
        crown.transform.parent = p;
    }

    static void Lamp(Vector3 pos, Transform p)
    {
        var pole = Make(PrimitiveType.Cylinder, null, pos + new Vector3(0, 2f, 0), new Vector3(0.05f, 4, 0.05f));
        Paint(pole, new Color(0.2f, 0.2f, 0.2f));
        pole.transform.parent = p;
        var arm = Make(PrimitiveType.Cube, null, pos + new Vector3(0, 4.2f, 0.3f), new Vector3(0.06f, 0.06f, 0.6f));
        Paint(arm, new Color(0.2f, 0.2f, 0.2f));
        arm.transform.parent = p;
        var bulb = Make(PrimitiveType.Sphere, null, pos + new Vector3(0, 4.2f, 0.7f), new Vector3(0.1f, 0.1f, 0.1f));
        Paint(bulb, new Color(1f, 0.9f, 0.6f));
        bulb.transform.parent = p;
        var pl = new GameObject("PointLight");
        pl.transform.SetParent(p);
        pl.transform.position = pos + new Vector3(0, 3.5f, 0.7f);
        var lt = pl.AddComponent<Light>();
        lt.type = LightType.Point;
        lt.color = new Color(1f, 0.85f, 0.5f);
        lt.intensity = 0.5f;
        lt.range = 10f;
    }

    static void Tower(Vector3 pos, Transform p)
    {
        float w = 24, d = 24, h = 100, th = 0.5f, dh = 4f, dw = 6f;
        var wallMat = new Material(Shader.Find("Standard")) { color = new Color(0.03f, 0.035f, 0.085f) };
        wallMat.EnableKeyword("_EMISSION");
        wallMat.SetColor("_EmissionColor", new Color(0.04f, 0.04f, 0.05f));
        var bandMat = new Material(Shader.Find("Standard")) { color = new Color(0.18f, 0.18f, 0.2f) };
        bandMat.SetFloat("_Glossiness", 0.8f);
        bandMat.EnableKeyword("_EMISSION");
        bandMat.SetColor("_EmissionColor", new Color(0.04f, 0.04f, 0.05f));
        var darkMat = new Material(Shader.Find("Standard")) { color = new Color(0.04f, 0.05f, 0.04f) };
        darkMat.EnableKeyword("_EMISSION");
        darkMat.SetColor("_EmissionColor", new Color(0.04f, 0.04f, 0.05f));
        var floorMat = new Material(Shader.Find("Standard")) { color = new Color(0.02f, 0.02f, 0.02f) };
        floorMat.EnableKeyword("_EMISSION");
        floorMat.SetColor("_EmissionColor", new Color(0.035f, 0.035f, 0.04f));

        // Back wall
        var bw = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h / 2, -d / 2 + th / 2), new Vector3(w, h, th));
        bw.GetComponent<MeshRenderer>().material = wallMat;
        bw.transform.parent = p;
        // Left wall
        var lw = Make(PrimitiveType.Cube, null, pos + new Vector3(-w / 2 + th / 2, h / 2, 0), new Vector3(th, h, d));
        lw.GetComponent<MeshRenderer>().material = wallMat;
        lw.transform.parent = p;
        // Right wall
        var rw = Make(PrimitiveType.Cube, null, pos + new Vector3(w / 2 - th / 2, h / 2, 0), new Vector3(th, h, d));
        rw.GetComponent<MeshRenderer>().material = wallMat;
        rw.transform.parent = p;

        // Front wall sections (with door gap)
        float fw = (w - dw - th) / 2;
        var fwl = Make(PrimitiveType.Cube, null, pos + new Vector3(-dw / 2 - fw / 2 - th / 2, h / 2, d / 2 - th / 2), new Vector3(fw, h, th));
        fwl.GetComponent<MeshRenderer>().material = wallMat;
        fwl.transform.parent = p;
        var fwr = Make(PrimitiveType.Cube, null, pos + new Vector3(dw / 2 + fw / 2 + th / 2, h / 2, d / 2 - th / 2), new Vector3(fw, h, th));
        fwr.GetComponent<MeshRenderer>().material = wallMat;
        fwr.transform.parent = p;
        // Door header
        var dhdr = Make(PrimitiveType.Cube, null, pos + new Vector3(0, dh, d / 2 - th / 2), new Vector3(dw, th, th));
        dhdr.GetComponent<MeshRenderer>().material = wallMat;
        dhdr.transform.parent = p;

        // Corner pillars
        float pw = th * 2;
        Vector3[] cps = {
            new Vector3(-w / 2 + pw / 2, h / 2, -d / 2 + pw / 2),
            new Vector3(w / 2 - pw / 2, h / 2, -d / 2 + pw / 2),
            new Vector3(-w / 2 + pw / 2, h / 2, d / 2 - pw / 2),
            new Vector3(w / 2 - pw / 2, h / 2, d / 2 - pw / 2),
        };
        foreach (var cp in cps)
        {
            var pillar = Make(PrimitiveType.Cube, null, pos + cp, new Vector3(pw, h, pw));
            pillar.GetComponent<MeshRenderer>().material = darkMat;
            pillar.transform.parent = p;
        }

        // Floor layout (8m per floor = 4x player height)
        float floorH = 8f;
        int numFloors = Mathf.FloorToInt((h - dh) / floorH);

        // Ground floor slab (entrance level, empty, with stairwell hole)
        SlabWithHole(pos, dh, w, d, floorMat, p, 9.5f, 0f);

        // Upper floors with rooms
        for (int f = 1; f < numFloors; f++)
        {
            float yPos = dh + f * floorH;
            AddFloor(pos, yPos, w, d, floorH, floorMat, p);
        }

        // Continuous stairs through stairwell hole (14 steps per floor)
        float stairX = -w / 2 + 1.5f;
        float stairZ = -d / 2 + 1.5f;
        float rise = 0.25f;
        float run = 0.30f;
        float slabTop = dh + 0.1f;
        int stepsPerF = Mathf.FloorToInt(floorH / rise);
        int totalSteps = (numFloors - 1) * stepsPerF;
        for (int s = 0; s < totalSteps; s++)
        {
            int fIdx = s / stepsPerF;
            int sIdx = s % stepsPerF;
            float sy = slabTop + fIdx * floorH + (sIdx + 0.5f) * rise;
            float sz = stairZ + sIdx * run;
            var step = Make(PrimitiveType.Cube, null, pos + new Vector3(stairX, sy, sz), new Vector3(1.2f, rise, run));
            step.GetComponent<MeshRenderer>().material = darkMat;
            step.transform.parent = p;
        }

        // Window bands
        for (int i = 0; i < 22; i++)
        {
            float y = (i + 1) * 4.5f;
            if (y < dh + 0.5f) continue;
            var band = Make(PrimitiveType.Cube, null, pos + new Vector3(0, y, 0), new Vector3(w - 0.4f, 0.1f, d - 0.4f));
            band.GetComponent<MeshRenderer>().material = bandMat;
            band.transform.parent = p;
        }

        // Roof crown
        var crown = Make(PrimitiveType.Cube, null, pos + new Vector3(0, h + 1, 0), new Vector3(w + 2, 2, d + 2));
        crown.GetComponent<MeshRenderer>().material = darkMat;
        crown.transform.parent = p;
        // Antenna
        var spire = Make(PrimitiveType.Cylinder, null, pos + new Vector3(0, h + 6, 0), new Vector3(0.6f, 8, 0.6f));
        Paint(spire, Color.gray);
        spire.transform.parent = p;
    }

    static void SlabWithHole(Vector3 pos, float y, float w, float d, Material mat, Transform p, float holeX, float holeZ)
    {
        float hw = (w - 0.4f) / 2;
        float hd = (d - 0.4f) / 2;
        var main = Make(PrimitiveType.Cube, null, pos + new Vector3((-holeX + hw) / 2, y + 0.05f, 0), new Vector3(holeX + hw, 0.1f, hd * 2));
        main.GetComponent<MeshRenderer>().material = mat;
        main.transform.parent = p;
        var left = Make(PrimitiveType.Cube, null, pos + new Vector3((-hw - holeX) / 2, y + 0.05f, (-holeZ + hd) / 2), new Vector3(hw - holeX, 0.1f, holeZ + hd));
        left.GetComponent<MeshRenderer>().material = mat;
        left.transform.parent = p;
    }

    static void AddFloor(Vector3 pos, float y, float w, float d, float floorH, Material floorMat, Transform p)
    {
        float wallTh = 0.15f;
        float doorW = 1.2f;
        float cx = y + floorH / 2;

        // Floor slab (with stairwell hole in back-left corner)
        SlabWithHole(pos, y, w, d, floorMat, p, 9.5f, 0f);

        // Interior walls along X (at x = -6, 0, 6)
        float[] xWalls = { -6f, 0f, 6f };
        float[] zDoors = { -9f, -3f, 3f, 9f };
        float[] zDoorsLeft = { -9f, -3f, 0f, 3f, 9f }; // extra stairwell exit door for left wall
        float halfD = d / 2;
        foreach (float wx in xWalls)
        {
            float prev = -halfD;
            float[] doors = (wx == -6f) ? zDoorsLeft : zDoors;
            for (int i = 0; i < doors.Length; i++)
            {
                float cur = doors[i];
                float hw = (cur == 0f) ? 0.9f : doorW / 2;
                float ds = cur - hw;
                float de = cur + hw;
                if (ds > prev)
                {
                    float len = ds - prev;
                    var seg = Make(PrimitiveType.Cube, null, pos + new Vector3(wx, cx, prev + len / 2), new Vector3(wallTh, floorH, len));
                    seg.GetComponent<MeshRenderer>().material = floorMat;
                    seg.transform.parent = p;
                }
                prev = de;
            }
            if (prev < halfD)
            {
                float len = halfD - prev;
                var seg = Make(PrimitiveType.Cube, null, pos + new Vector3(wx, cx, prev + len / 2), new Vector3(wallTh, floorH, len));
                seg.GetComponent<MeshRenderer>().material = floorMat;
                seg.transform.parent = p;
            }
        }

        // Interior walls along Z (at z = -6, 0, 6)
        float[] zWalls = { -6f, 0f, 6f };
        float[] xDoors = { -9f, -3f, 3f, 9f };
        float[] xDoorsBack = { -10.5f, -9f, -3f, 3f, 9f }; // extra stairwell door at back wall
        float[] xDoorsMid = { -10.5f, -9f, -3f, 3f, 9f }; // extra stairwell exit door at z=0
        float halfW = w / 2;
        foreach (float wz in zWalls)
        {
            float prev = -halfW;
            float[] doors = (wz == -6f) ? xDoorsBack : (wz == 0f) ? xDoorsMid : xDoors;
            for (int i = 0; i < doors.Length; i++)
            {
                float cur = doors[i];
                float hw = (cur == -10.5f) ? 0.9f : doorW / 2;
                float ds = cur - hw;
                float de = cur + hw;
                if (ds > prev)
                {
                    float len = ds - prev;
                    var seg = Make(PrimitiveType.Cube, null, pos + new Vector3(prev + len / 2, cx, wz), new Vector3(len, floorH, wallTh));
                    seg.GetComponent<MeshRenderer>().material = floorMat;
                    seg.transform.parent = p;
                }
                prev = de;
            }
            if (prev < halfW)
            {
                float len = halfW - prev;
                var seg = Make(PrimitiveType.Cube, null, pos + new Vector3(prev + len / 2, cx, wz), new Vector3(len, floorH, wallTh));
                seg.GetComponent<MeshRenderer>().material = floorMat;
                seg.transform.parent = p;
            }
        }
    }

    static void BlackMonster(Vector3 pos, Transform p)
    {
        var root = new GameObject("MonsterRoot");
        root.transform.position = pos;
        root.transform.parent = p;
        root.AddComponent<MonsterController>();
        var col = root.AddComponent<CapsuleCollider>();
        col.height = 2.2f;
        col.radius = 0.4f;
        col.isTrigger = true;
        var mat = new Material(Shader.Find("Standard")) { color = new Color(0.0f, 0.8f, 0.0f) };
        mat.EnableKeyword("_EMISSION");
        mat.SetColor("_EmissionColor", new Color(0.0f, 0.5f, 0.0f));

        var eyeMat = new Material(Shader.Find("Standard")) { color = Color.red };
        eyeMat.EnableKeyword("_EMISSION");
        eyeMat.SetColor("_EmissionColor", Color.red * 3f);

        var body = Make(PrimitiveType.Cube, null, Vector3.zero, new Vector3(0.8f, 1.2f, 0.4f));
        body.GetComponent<MeshRenderer>().material = mat;
        body.transform.SetParent(root.transform, false);

        var head = Make(PrimitiveType.Cube, null, new Vector3(0, 0.9f, 0), new Vector3(0.5f, 0.4f, 0.35f));
        head.GetComponent<MeshRenderer>().material = mat;
        head.transform.SetParent(root.transform, false);

        var eyeL = Make(PrimitiveType.Sphere, null, new Vector3(-0.12f, 0.95f, 0.2f), new Vector3(0.08f, 0.08f, 0.05f));
        eyeL.GetComponent<MeshRenderer>().material = eyeMat;
        eyeL.transform.SetParent(root.transform, false);

        var eyeR = Make(PrimitiveType.Sphere, null, new Vector3(0.12f, 0.95f, 0.2f), new Vector3(0.08f, 0.08f, 0.05f));
        eyeR.GetComponent<MeshRenderer>().material = eyeMat;
        eyeR.transform.SetParent(root.transform, false);

        var lArm = Make(PrimitiveType.Cube, null, new Vector3(-0.55f, 0.3f, 0), new Vector3(0.15f, 0.8f, 0.15f));
        lArm.GetComponent<MeshRenderer>().material = mat;
        lArm.transform.SetParent(root.transform, false);

        var rArm = Make(PrimitiveType.Cube, null, new Vector3(0.55f, 0.3f, 0), new Vector3(0.15f, 0.8f, 0.15f));
        rArm.GetComponent<MeshRenderer>().material = mat;
        rArm.transform.SetParent(root.transform, false);

        var lLeg = Make(PrimitiveType.Cube, null, new Vector3(-0.2f, -0.5f, 0), new Vector3(0.2f, 0.6f, 0.2f));
        lLeg.GetComponent<MeshRenderer>().material = mat;
        lLeg.transform.SetParent(root.transform, false);

        var rLeg = Make(PrimitiveType.Cube, null, new Vector3(0.2f, -0.5f, 0), new Vector3(0.2f, 0.6f, 0.2f));
        rLeg.GetComponent<MeshRenderer>().material = mat;
        rLeg.transform.SetParent(root.transform, false);

        var glow = new GameObject("MonsterGlow");
        glow.transform.SetParent(root.transform, false);
        glow.transform.localPosition = new Vector3(0, 1, 0);
        var gl = glow.AddComponent<Light>();
        gl.type = LightType.Point;
        gl.color = Color.green;
        gl.intensity = 0.6f;
        gl.range = 8f;

        foreach (var c in root.GetComponentsInChildren<Collider>())
            if (c.gameObject != root)
                Object.DestroyImmediate(c);
    }

    static void Car(Vector3 pos, Color c, Transform p)
    {
        float ry = 0.02f;
        var body = Make(PrimitiveType.Cube, null, pos + new Vector3(0, ry + 0.3f, 0), new Vector3(2.5f, 0.5f, 1.2f));
        Paint(body, c);
        body.transform.parent = p;
        var cab = Make(PrimitiveType.Cube, null, pos + new Vector3(0, ry + 0.7f, -0.1f), new Vector3(1.4f, 0.5f, 0.9f));
        Paint(cab, c * 0.8f);
        cab.transform.parent = p;
    }

    static void TL(Vector3 pos, Transform p)
    {
        var pole = Make(PrimitiveType.Cylinder, null, pos + new Vector3(0, 1.5f, 0), new Vector3(0.08f, 3, 0.08f));
        Paint(pole, Color.gray);
        pole.transform.parent = p;
        var box = Make(PrimitiveType.Cube, null, pos + new Vector3(0, 3.5f, 0), new Vector3(0.4f, 0.4f, 0.2f));
        Paint(box, Color.black);
        box.transform.parent = p;
        var cls = new[] { Color.red, Color.yellow, Color.green };
        for (int i = 0; i < 3; i++)
        {
            var l = Make(PrimitiveType.Cube, null, pos + new Vector3(0, 3.2f + i * 0.3f, 0.15f), new Vector3(0.12f, 0.12f, 0.02f));
            Paint(l, cls[i]);
            l.transform.parent = p;
        }
    }

    static void AttachGun(Transform cam)
    {
        var root = new GameObject("Gun");
        root.transform.SetParent(cam, false);
        root.transform.localPosition = new Vector3(0.3f, -0.25f, 0.5f);

        var gunMat = new Material(Shader.Find("Standard")) { color = new Color(0.15f, 0.15f, 0.15f) };
        var darkMat = new Material(Shader.Find("Standard")) { color = new Color(0.05f, 0.05f, 0.05f) };
        var woodMat = new Material(Shader.Find("Standard")) { color = new Color(0.25f, 0.12f, 0.05f) };

        var body = Make(PrimitiveType.Cube, null, new Vector3(0, 0, 0.2f), new Vector3(0.06f, 0.08f, 0.5f));
        body.GetComponent<MeshRenderer>().material = gunMat;
        Object.DestroyImmediate(body.GetComponent<Collider>());
        body.transform.SetParent(root.transform, false);

        var barrel = Make(PrimitiveType.Cube, null, new Vector3(0, 0.02f, 0.55f), new Vector3(0.03f, 0.03f, 0.3f));
        barrel.GetComponent<MeshRenderer>().material = darkMat;
        Object.DestroyImmediate(barrel.GetComponent<Collider>());
        barrel.transform.SetParent(root.transform, false);

        var grip = Make(PrimitiveType.Cube, null, new Vector3(0, -0.1f, -0.1f), new Vector3(0.04f, 0.1f, 0.04f));
        grip.GetComponent<MeshRenderer>().material = woodMat;
        Object.DestroyImmediate(grip.GetComponent<Collider>());
        grip.transform.SetParent(root.transform, false);

        var slide = Make(PrimitiveType.Cube, null, new Vector3(0, 0.05f, 0.2f), new Vector3(0.04f, 0.02f, 0.3f));
        slide.GetComponent<MeshRenderer>().material = darkMat;
        Object.DestroyImmediate(slide.GetComponent<Collider>());
        slide.transform.SetParent(root.transform, false);

        var mag = Make(PrimitiveType.Cube, null, new Vector3(0, -0.06f, 0.15f), new Vector3(0.04f, 0.04f, 0.08f));
        mag.GetComponent<MeshRenderer>().material = darkMat;
        Object.DestroyImmediate(mag.GetComponent<Collider>());
        mag.transform.SetParent(root.transform, false);

        root.AddComponent<GunController>();
    }
}
