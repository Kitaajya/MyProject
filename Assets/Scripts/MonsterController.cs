using UnityEngine;

public class MonsterController : MonoBehaviour
{
    public float moveSpeed = 4f;
    public float attackRange = 1.8f;
    public float detectRange = 25f;
    public float fovAngle = 120f;
    public float maxHealth = 100f;

    private float currentHealth;
    private Transform player;
    private float attackCooldown;

    void Start()
    {
        currentHealth = maxHealth;
        var go = GameObject.Find("Player");
        if (go != null) player = go.transform;
    }

    public void TakeDamage(float amount)
    {
        currentHealth -= amount;
        if (currentHealth <= 0f)
            Destroy(gameObject);
    }

    void Update()
    {
        if (player == null) return;

        float dist = HDist(transform.position, player.position);

        if (dist > detectRange) return;
        float angle = Vector3.Angle(transform.forward, (player.position - transform.position).normalized);
        if (angle > fovAngle * 0.5f) return;

        Vector3 origin = transform.position + Vector3.up * 1.3f;
        Vector3 toPlayer = (player.position - origin).normalized;
        RaycastHit hit;
        if (Physics.Raycast(origin, toPlayer, out hit, dist + 1f))
            if (!hit.transform.CompareTag("Player"))
                return;

        Vector3 moveDir = (player.position - transform.position).normalized;
        moveDir.y = 0;
        transform.rotation = Quaternion.LookRotation(moveDir);
        transform.position += moveDir * moveSpeed * Time.deltaTime;

        if (dist < attackRange)
        {
            attackCooldown -= Time.deltaTime;
            if (attackCooldown <= 0f)
            {
                PlayerController pc = player.GetComponent<PlayerController>();
                if (pc != null) pc.TakeDamage(10);
                attackCooldown = 1.2f;
            }
        }
    }

    float HDist(Vector3 a, Vector3 b)
    {
        Vector3 d = a - b;
        d.y = 0;
        return d.magnitude;
    }

    void OnDrawGizmos()
    {
        if (player == null) return;

        Vector3 origin = transform.position + Vector3.up * 1.3f;

        Gizmos.color = Color.cyan;
        Gizmos.DrawWireSphere(origin, detectRange);

        float half = fovAngle * 0.5f;
        Vector3 fwd = transform.forward * detectRange;
        Gizmos.DrawRay(origin, Quaternion.Euler(0, -half, 0) * fwd);
        Gizmos.DrawRay(origin, Quaternion.Euler(0, half, 0) * fwd);

        Vector3 toPlayer = player.position - transform.position;
        float dist = HDist(transform.position, player.position);
        bool canSee = dist <= detectRange &&
            Vector3.Angle(transform.forward, toPlayer.normalized) <= half;

        if (canSee)
        {
            Vector3 dir = (player.position - origin).normalized;
            RaycastHit hit;
            if (Physics.Raycast(origin, dir, out hit, dist + 1f))
                canSee = hit.transform.CompareTag("Player");
        }

        Gizmos.color = canSee ? Color.green : Color.red;
        Gizmos.DrawLine(origin, player.position);
    }
}
