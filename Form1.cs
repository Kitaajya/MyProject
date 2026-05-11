using System.Diagnostics.Eventing.Reader;

namespace TestHomeWork1 {
    public partial class Form1 : Form {
        public Form1() {
            InitializeComponent();
        }

        void F(string a) {
            textBox1.Text += a;
        }
        private void button1_Click(object sender, EventArgs e) {
            F("1");
        }

        private void button2_Click(object sender, EventArgs e) {
            F("2");
        }

        private void button4_Click(object sender, EventArgs e) {
            F("4");
        }


        private void textBox1_TextChanged(object sender, EventArgs e) {

        }

        private void button3_Click(object sender, EventArgs e) {
            F("3");
        }

        private void button5_Click(object sender, EventArgs e) {
            F("5");
        }

        private void button6_Click(object sender, EventArgs e) {
            F("6");
        }

        private void button7_Click(object sender, EventArgs e) {
            F("7");
        }

        private void button8_Click(object sender, EventArgs e) {
            F("8");
        }

        private void button9_Click(object sender, EventArgs e) {
            F("9");
        }

        private void button10_Click(object sender, EventArgs e) {
            F("0");
        }

        private void button15_Click(object sender, EventArgs e) {
            textBox1.Text = null;
        }
        double a = 0;
        string p = "";
        //加法
        private void button11_Click(object sender, EventArgs e) {
            a = Convert.ToDouble(textBox1.Text);
            p = "+";
            textBox1.Clear();
        }
        //减法
        private void button12_Click(object sender, EventArgs e) {
            p = "-";
            a = Convert.ToDouble(textBox1.Text);
            textBox1.Clear();
        }
        //等于
        private void button16_Click(object sender, EventArgs e) {
            double b = Convert.ToDouble(textBox1.Text);
            if (p.Equals("+")) textBox1.Text = Convert.ToString(a + b);
            if (p.Equals("-")) textBox1.Text = Convert.ToString(a - b);
            if (p.Equals("*")) textBox1.Text = Convert.ToString(a * b);
            if (p.Equals("/")) textBox1.Text = Convert.ToString(a / b);
        }
        //乘法
        private void button13_Click(object sender, EventArgs e) {
            a = Convert.ToDouble(textBox1.Text);
            p = "*";
            textBox1.Clear();
        }
        //除法
        private void button14_Click(object sender, EventArgs e) {
            a = Convert.ToDouble(textBox1.Text);
            p = "/";
            textBox1.Clear();
        }

        private void Form1_Load(object sender, EventArgs e) {

        }
        //小数点
        private void button17_Click(object sender, EventArgs e) {
            F(".");
        }
        
    }
}
