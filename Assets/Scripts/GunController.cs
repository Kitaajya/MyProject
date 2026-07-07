using UnityEngine;

public class GunController : MonoBehaviour
{
    public int magSize = 30;
    public float fireRate = 0.1f;
    public float reloadTime = 2f;
    public float damage = 20f;
    public float range = 100f;

    private int currentAmmo;
    private float fireTimer;
    private float reloadTimer;
    private bool isReloading;
    private Camera cam;
    private Vector3 origPos;
    private Quaternion origRot;
    private Vector3 recoilOffset;
    private Vector3 recoilRotOffset;

    void Start()
    {
        currentAmmo = magSize;
        cam = GetComponentInParent<Camera>();
        origPos = transform.localPosition;
        origRot = transform.localRotation;
    }

    void Update()
    {
        if (isReloading)
        {
            reloadTimer -= Time.deltaTime;
            if (reloadTimer <= 0f)
            {
                currentAmmo = magSize;
                isReloading = false;
            }
            return;
        }

        fireTimer -= Time.deltaTime;

        if (Input.GetMouseButton(0) && fireTimer <= 0f && currentAmmo > 0)
        {
            Shoot();
            fireTimer = fireRate;
        }

        if (Input.GetKeyDown(KeyCode.R))
        {
            StartReload();
        }

        if (currentAmmo <= 0)
        {
            StartReload();
        }

        recoilOffset = Vector3.Lerp(recoilOffset, Vector3.zero, Time.deltaTime * 12f);
        recoilRotOffset = Vector3.Lerp(recoilRotOffset, Vector3.zero, Time.deltaTime * 12f);

        Vector3 targetPos = origPos + recoilOffset;
        Quaternion targetRot = origRot * Quaternion.Euler(recoilRotOffset);

        if (isReloading)
        {
            float t = 1f - reloadTimer / reloadTime;
            float drop = t < 0.3f ? t / 0.3f : (t > 0.7f ? (1f - t) / 0.3f : 1f);
            targetPos += new Vector3(0, -0.15f * drop, -0.1f * drop);
            targetRot *= Quaternion.Euler(-15f * drop, 0, 0);
        }

        transform.localPosition = Vector3.Lerp(transform.localPosition, targetPos, Time.deltaTime * 15f);
        transform.localRotation = Quaternion.Slerp(transform.localRotation, targetRot, Time.deltaTime * 15f);
    }

    void Shoot()
    {
        currentAmmo--;
        recoilOffset += new Vector3(0, 0, -0.04f);
        recoilRotOffset += new Vector3(-3f, Random.Range(-0.8f, 0.8f), 0);

        if (cam == null) return;

        Ray ray = new Ray(cam.transform.position, cam.transform.forward);
        RaycastHit hit;
        if (Physics.Raycast(ray, out hit, range))
        {
            MonsterController mc = hit.transform.root.GetComponent<MonsterController>();
            if (mc != null) mc.TakeDamage(damage);
        }

        Vector3 pos = transform.position + cam.transform.forward * 0.3f;
        GameObject flash = GameObject.CreatePrimitive(PrimitiveType.Sphere);
        DestroyImmediate(flash.GetComponent<Collider>());
        flash.transform.position = pos;
        flash.transform.localScale = Vector3.one * 0.04f;
        var rend = flash.GetComponent<MeshRenderer>();
        rend.material = new Material(Shader.Find("Standard")) { color = Color.yellow };
        rend.enabled = true;
        Destroy(flash, 0.05f);
    }

    void StartReload()
    {
        if (isReloading || currentAmmo == magSize) return;
        isReloading = true;
        reloadTimer = reloadTime;
    }

    public bool IsReloading()
    {
        return isReloading;
    }

    public int GetCurrentAmmo()
    {
        return currentAmmo;
    }

    public int GetMagSize()
    {
        return magSize;
    }

    public float GetReloadProgress()
    {
        return isReloading ? 1f - reloadTimer / reloadTime : 1f;
    }

    public void TakeDamage(float amount)
    {
    }

    void OnGUI()
    {
        string ammoText = isReloading
            ? "换弹中... " + Mathf.RoundToInt((1f - reloadTimer / reloadTime) * 100f) + "%"
            : currentAmmo + " / " + magSize;

        GUIStyle style = new GUIStyle();
        style.fontSize = 24;
        style.normal.textColor = Color.white;
        style.fontStyle = FontStyle.Bold;

        GUI.Label(new Rect(Screen.width - 200, Screen.height - 60, 180, 40), ammoText, style);
    }
}
