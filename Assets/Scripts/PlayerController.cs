using UnityEngine;

public class PlayerController : MonoBehaviour
{
    public float moveSpeed = 5f;
    public float lookSpeed = 2f;
    public float jumpForce = 5f;
    public float flySpeed = 25f;
    public int maxHealth = 100;
    public float bobFrequency = 10f;
    public float bobAmplitude = 0.06f;

    private int currentHealth;
    private Rigidbody rb;
    private float rotationX = 0f;
    private bool isGrounded;
    private bool isFlying;
    private float bobTimer;
    private float defaultCamY;

    void Start()
    {
        currentHealth = maxHealth;
        rb = GetComponent<Rigidbody>();
        Cursor.lockState = CursorLockMode.Locked;
        if (Camera.main != null)
            defaultCamY = Camera.main.transform.localPosition.y;
    }

    public void TakeDamage(int amount)
    {
        currentHealth -= amount;
        Debug.Log("玩家受伤! 生命值: " + currentHealth + "/" + maxHealth);
        if (currentHealth <= 0)
        {
            Debug.Log("玩家死亡!");
            Destroy(gameObject);
        }
    }

    void Update()
    {
        float mouseX = Input.GetAxis("Mouse X") * lookSpeed;
        float mouseY = Input.GetAxis("Mouse Y") * lookSpeed;

        rotationX -= mouseY;
        rotationX = Mathf.Clamp(rotationX, -90f, 90f);
        Camera.main.transform.localRotation = Quaternion.Euler(rotationX, 0f, 0f);
        transform.Rotate(Vector3.up * mouseX);

        if (Input.GetKeyDown(KeyCode.F))
        {
            isFlying = !isFlying;
            rb.useGravity = !isFlying;
        }

        if (!isFlying && Input.GetKeyDown(KeyCode.Space) && isGrounded)
        {
            rb.AddForce(Vector3.up * jumpForce, ForceMode.Impulse);
            isGrounded = false;
        }

        float h = Input.GetAxis("Horizontal");
        float v = Input.GetAxis("Vertical");
        bool isMoving = (Mathf.Abs(h) > 0.1f || Mathf.Abs(v) > 0.1f) && isGrounded && !isFlying;

        if (isMoving)
        {
            bobTimer += Time.deltaTime * bobFrequency * Mathf.Max(Mathf.Abs(h), Mathf.Abs(v));
            float bob = Mathf.Sin(bobTimer) * bobAmplitude;
            Camera.main.transform.localPosition = new Vector3(
                Camera.main.transform.localPosition.x,
                defaultCamY + bob,
                Camera.main.transform.localPosition.z
            );
        }
        else
        {
            bobTimer = 0f;
            Camera.main.transform.localPosition = Vector3.Lerp(
                Camera.main.transform.localPosition,
                new Vector3(Camera.main.transform.localPosition.x, defaultCamY, Camera.main.transform.localPosition.z),
                Time.deltaTime * 10f
            );
        }
    }

    void FixedUpdate()
    {
        float h = Input.GetAxis("Horizontal");
        float v = Input.GetAxis("Vertical");

        if (isFlying)
        {
            float up = 0f;
            if (Input.GetKey(KeyCode.Space)) up = 1f;
            if (Input.GetKey(KeyCode.LeftShift)) up = -1f;
            Vector3 move = transform.right * h + transform.forward * v + Vector3.up * up;
            rb.velocity = move * flySpeed;
        }
        else
        {
            Vector3 move = transform.right * h + transform.forward * v;
            rb.velocity = new Vector3(move.x * moveSpeed, rb.velocity.y, move.z * moveSpeed);
            isGrounded = Physics.Raycast(transform.position, Vector3.down, 1.1f);
        }
    }
}
