namespace App.Models
{
    public class Entity
    {
        public long Id { get; set; }
        public string Name { get; set; }
        public string Email { get; set; }
        public int Status { get; set; }
        public bool Active { get; set; }
        public double Balance { get; set; }
        
        public int Code => (int)(Id % 1000);
        public long Timestamp => System.DateTimeOffset.UtcNow.ToUnixTimeMilliseconds();
    }
}

