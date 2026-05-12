namespace GomokuGame {
    public partial class Form1 : Form {
        private Button[,] board = new Button[6, 6];
        private int[,] state = new int[6, 6];
        private int turn = 1;
        private bool gameOver = false;

        public Form1() {
            InitializeComponent();
            SetupBoard();
        }

        //加载面板

        private void SetupBoard() {
            Button[,] map = {
                { button21, button1,  button2,  button5,  button4,  button3 },
                { button22, button6,  button7,  button8,  button9,  button10 },
                { button23, button11, button12, button13, button14, button15 },
                { button24, button16, button17, button18, button19, button20 },
                { button25, button26, button27, button28, button29, button30 },
                { button31, button32, button33, button34, button35, button36 }
            };

            for (int r = 0; r < 6; r++) {
                for (int c = 0; c < 6; c++) {
                    board[r, c] = map[r, c];//所有加载面板里的按钮全部复制到面板里
                    int row = r, col = c;
                    board[r, c].Click += (s, e) => OnButtonClick(row, col);
                }
            }
            textBox1.Text = "红方先行";
        }

        private void OnButtonClick(int row, int col) {
            if (gameOver || state[row, col] != 0) return;

            state[row, col] = turn;
            board[row, col].BackColor = turn == 1 ? Color.Red : Color.Blue;

            if (CheckWin(row, col, turn)) {
                textBox1.Text = turn == 1 ? "红方获胜！" : "蓝方获胜！";
                gameOver = true;
                return;
            }

            if (IsBoardFull()) {
                textBox1.Text = "平局！";
                gameOver = true;
                return;
            }

            turn = turn == 1 ? 2 : 1;
            textBox1.Text = turn == 1 ? "轮到红方" : "轮到蓝方";
        }

        private bool CheckWin(int row, int col, int player) {
            int[][] dirs = {
                [ 0, 1 ],
                [ 1, 0 ],
                [ 1, 1 ],
                [ 1, -1]};

            foreach (var dir in dirs) {
                int count = 1;
                count += CountDir(row, col, dir[0], dir[1], player);
                count += CountDir(row, col, -dir[0], -dir[1], player);
                if (count >= 5) return true;
            }
            return false;
        }

        private int CountDir(int row, int col, int dr, int dc, int player) {
            int r = row + dr, c = col + dc;
            int count = 0;
            while (r >= 0 && r < 6 && c >= 0 && c < 6 && state[r, c] == player) {
                count++;
                r += dr;
                c += dc;
            }
            return count;
        }

        private bool IsBoardFull() {
            for (int r = 0; r < 6; r++)
                for (int c = 0; c < 6; c++)
                    if (state[r, c] == 0) return false;
            return true;
        }
    }
}
