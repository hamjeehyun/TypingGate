import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.Vector;

public class AcidRainGame extends JFrame implements ActionListener, Runnable {
	static final int WIDTH = 1000;
	static final int HEIGHT = 800;
	int life = 5;//������
	int score=0;//score �����ϴ°�
	int speed = 1;//�ӵ� ����
	int twinkle =0;// extreme �����̰� �ϴ°�
	boolean Running =false;//���� ����������
	boolean First = true;//��ó�� �϶� ����
	boolean NFirst = true;//��ó�� �븻������ �ӵ� ����
	boolean HFirst = true;//��ó�� �ϵ� ������ �ӵ�
	boolean EFirst = true;// ����
	String Ranking ="";//��ŷ�� ���� ����
	Vector<String> words;//�ܾ�
	Vector<Word> viewingWords;//�������� �ܾ�� 
	BufferedReader inputStream;//���� �̸�
	Thread t;
	long time;
	int tempspeed=1;

	DropArea da1;//�ܾ �������� �ִ±���
	JTextField t1;//�ؽ�Ʈ �ڽ� �Է��ϴ°�
	JPanel buttons;// start howtoplay rangking �ִ� �г�
	JSplitPane splitPane;// �г��� �׸��ִ� ������ ��ư �������� ����
	JLabel imageLabel1,imageLabel2,imageLabel3,imageLabel4;
	JPanel Pause;//�������� ȭ�� ����
	JLabel PLabel;

	void createMenu(){
		JMenuBar mb = new JMenuBar();
		//setting �޴�
		JMenuItem [] menuItem =new JMenuItem[3];
		String[] itemTitle = {"������","������", "����Ұ�"};
		JMenu settingMenu = new JMenu("Setting");

		for(int i=0;i<menuItem.length;i++){
			menuItem[i] =new JMenuItem(itemTitle[i]);
			menuItem[i].addActionListener(new MenuActionListener());

			settingMenu.add(menuItem[i]);
		}
		//run �޴�
		JMenuItem [] menuItem1 =new JMenuItem[2];
		String[] itemTitle1 = {"esc","resume"};
		JMenu runMenu = new JMenu("Run");

		for(int i=0;i<menuItem1.length;i++){
			menuItem1[i] =new JMenuItem(itemTitle1[i]);
			menuItem1[i].addActionListener(new MenuActionListener1());

			runMenu.add(menuItem1[i]);
		}
		mb.add(runMenu);
		mb.add(settingMenu);
		setJMenuBar(mb);

	}
	class MenuActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String cmd = e.getActionCommand();
			if(cmd.equals("������")){
				JOptionPane.showMessageDialog(null, "mdae by ������,������","������",JOptionPane.CLOSED_OPTION);
			}
			else if(cmd.equals("������")){
				JOptionPane.showMessageDialog(null, "�ڹ��� Swing,Thread ������ ���� �� �ִ� ������ �����غ����� ����ԵǾ���."
						,"������",JOptionPane.CLOSED_OPTION);
			}
			else {
				JOptionPane.showMessageDialog(null, "������: ó���� Ÿ�ڰ����� ������� �ϴϱ� �ƴ°� �����ؼ� �����߾���.\n"
						+ "�׷��� ģ���� ���� ���X���X �ؼ� ������ �ѵ��ϴ�.\n "
						+ "������ ���� �͸� �ߴ� ������Ʈ�� ������ �Ǵ� ����� ���Ұ�,\n"
						+ "�� ������Ʈ�� ���� �� �� �ڹٸ� ���� ��� �� �ְ� �Ȱ� ���Ҵ�.\n\n"
						+ "������: �׻� ������ �������� ������Ʈ�� �Ҷ����� �� ������ �ʹ��� ��ư� �����\n"
						+ "�׷��� ���� § �ڵ尡 ������ �ǰ� ���𰡸� �̷ﳾ�� �� �ѵ����� �ʹ��� ����. �̰� ���� �׷���.\n"
						+ "��ư� ������� ���������� �ϳ��ϳ��� �� ������ ������ �Ǵ� �����̾���.\n"
						+ "�ƽ����� ���� ������Ʈ������ �ϴ� ���ȿ��� ��ſ� �������� ���� �� �־���.\n"
						+ "������Ʈ�� �������ֽ� ����̱����Բ� ������ ������ �帰��. ","����Ұ�",JOptionPane.CLOSED_OPTION);
			}
		}
	}
	class MenuActionListener1 implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String cmd = e.getActionCommand();
			if(cmd.equals("esc")){
				Pause = new JPanel();//�������� ȭ�� ����
				BorderLayout A= new BorderLayout();
				Pause.setLayout(A);
				PLabel= new JLabel("PAUSE",JLabel.CENTER);

				Running=false;
				da1.setVisible(false);
				splitPane.setVisible(false);
				t1.setVisible(false);//�ٸ� �͵� �Ⱥ��̰�
				Pause.setVisible(true);//�Ͻ����� ȭ�� ���̵���

				// �Ͻ����� ȭ�� ���� �߰�����
				tempspeed=speed;
				speed=0;
				add(Pause,BorderLayout.CENTER);
				Pause.add("North",PLabel);

			}
			else{
				da1.setVisible(true);
				splitPane.setVisible(true);
				t1.setVisible(true);
				Running=true;//���������� �ٲ�
				Pause.setVisible(false);//pause ȭ���� �ٽ� �Ⱥ��̵�����
				speed=tempspeed;
				Running=true;
			}
		}
	}

	public AcidRainGame() throws IOException, InterruptedException {//������
		// �ܾ� ��� �б�
		setTitle("Typing Gate");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		createMenu();
		try
		{
			AudioInputStream ais = AudioSystem.getAudioInputStream(new File("seo.wav"));
			Clip clip = AudioSystem.getClip();
			clip.stop();
			clip.open(ais);
			clip.start();
		}
		catch (Exception ex) {} 
		try {
			inputStream = new BufferedReader(new FileReader("words.txt"));//�ܾ��Ʈ ���ϵ�
		} catch (FileNotFoundException e) {//���� ������ ����ó��
			e.printStackTrace();
		}
		buttons = new JPanel();
		
		addButton(buttons, "Start",new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(First)
				{t.start();First=false;}//�����ϸ� �����尡 ���۵�
				Running=true;// ����������
			}
		});
		addButton(buttons, "How to Play",new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(null, "���ӹ��\n1.������� �ܾ �ϴܿ� �ִ� �Է�â���� �Է��� �� ENTER\n"
						+ "2.�˸��� �ܾ �Է��ϸ� �ܾ �������� 50���� �ö󰩴ϴ�\n"
						+ "3.�Է����� ���� �ܾ ������ �������� ����� �پ��ϴ�\n"
						+ "4.LEVEL�� �ö󰥼��� �ӵ��� �������ϴ�\n"
						+ "��ſ� ���ӵǼ���^_^");
			}
		});
		addButton(buttons, "Ranking",new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(null, Ranking);
			}//���⿡ ��ŷ ���� �߰� �ȴ�.
		});
		words = new Vector<String>();//���Ͽ��� �о�� �ܾ�� ����
		viewingWords = new Vector<Word>();//���������ִ� �ܾ��
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);//JSplitPane�� ����
		splitPane.setTopComponent(buttons);//������ ��ư�� �ִ�.
		imageLabel1 = new JLabel(new ImageIcon("cat1.jpg"));
		imageLabel2 = new JLabel(new ImageIcon("cat2.jpg"));
		imageLabel3 = new JLabel(new ImageIcon("cat3.jpg"));
		imageLabel4 = new JLabel(new ImageIcon("cat4.jpg"));

		String w;
		while((w = inputStream.readLine()) != null)
		{
			words.add(w);//���Ͽ��� �о�� �� words ���Ϳ� ����
		}
		da1 = new DropArea();//�������� �ܾ���� ����
		da1.setPreferredSize(new Dimension(WIDTH, HEIGHT));//ũ��

		add(da1, BorderLayout.CENTER);//������ ����� �߰�
		t1 = new JTextField(20);//�Էº�
		t1.addActionListener(this);
		Pause = new JPanel();//�������� ȭ�� ����
		BorderLayout A= new BorderLayout();

		Pause.setLayout(A);

		PLabel= new JLabel("PAUSE",JLabel.CENTER);

		JButton resume = new JButton("RESUME");//�̹�ư ������ �ٽ� �������� ���ư���.
		Pause.add("North",PLabel);
		Pause.add("Center",resume);

		resume.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//�ٽ� �ٸ� �͵��� ���̵��� �����
				da1.setVisible(true);
				splitPane.setVisible(true);
				t1.setVisible(true);
				Running=true;//���������� �ٲ�
				Pause.setVisible(false);//pause ȭ���� �ٽ� �Ⱥ��̵�����
				speed=tempspeed;
				Running=true;
			}

		});
		Pause.setVisible(false);//pause �� ó���� �Ⱥ��̵��� �����Ѵ�.

		t1.addKeyListener(new KeyAdapter(){//�ؽ�Ʈ �ڽ����� ����Ű ���� �� �̺�Ʈ
			public void keyPressed(KeyEvent e){
				if(Running)
					// Ű�� ���������� �̺�Ʈ
				{
					String s = e.getKeyText(e.getKeyCode()); // Ű��
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE){ // ESC ������
						Running=false;

						da1.setVisible(false);
						splitPane.setVisible(false);
						t1.setVisible(false);//�ٸ� �͵� �Ⱥ��̰�
						Pause.setVisible(true);//�Ͻ����� ȭ�� ���̵���

						// �Ͻ����� ȭ�� ���� �߰�����
						tempspeed=speed;
						speed=0;
						add(Pause,BorderLayout.CENTER);
					}}}}
				);

		add(t1, BorderLayout.SOUTH);
		splitPane.setContinuousLayout(true); 
		add(splitPane,BorderLayout.EAST);

		t = new Thread(this);

		pack();

		setVisible(true);

		// t.start();
	}
	public void addButton(Container c, String title, ActionListener listener) {
		JButton button = new JButton(title);
		c.add(button);
		button.addActionListener(listener);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		int index = -1;
		for(Word w : viewingWords) {
			if(t1.getText().equals(w.str))//�Էºο� ������ �׶��� index�� ã�Ƴ�
			{
				index = viewingWords.indexOf(w);
				score +=50;
			}
		}
		if(index != -1)
		{
			viewingWords.remove(index);//������ �ܾ� remove
			repaint();
		}
		t1.selectAll();

	}
	@SuppressWarnings("deprecation")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		splitPane.setBottomComponent(imageLabel1);
		while(true)
		{
			try{
				t.sleep(100/(speed+1));//�ӵ� ����
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			time++;
			if(speed!=0)
			{
				for(Word w : viewingWords)
				{
					w.y += 1;
				}
			}
			if(!viewingWords.isEmpty())
			{
				if(viewingWords.get(0).y > HEIGHT-10)//����������
				{
					life--;
					viewingWords.remove(0);
				}
			}

			if(life <= 0)
			{
				Integer wrap = new Integer(score);
				Ranking+= JOptionPane.showInputDialog(score+"�� - �̸��� �Է��ϼ���");
				Ranking+=" " + wrap.toString()+"��\n";
				//������ �������Ƿ� ���� ������ ���ؼ� �ٽ� �ʱ�ȭ ���ش�.
				score=0;
				life = 5;
				speed = 1;
				twinkle =0;
				Running=false;
				NFirst=true;
				HFirst=true;
				EFirst=true;
				splitPane.setBottomComponent(imageLabel1);
				//ImageIcon backgroundImg = new ImageIcon("cat5.jpg");
				words.clear();
				viewingWords.clear();
				try {
					inputStream = new BufferedReader(new FileReader("words.txt"));//�ܾ��Ʈ ���ϵ�
				} catch (FileNotFoundException e) {//���� ������ ����ó��
					e.printStackTrace();
				}
				String w;
				try {
					while((w = inputStream.readLine()) != null)
					{
						words.add(w);//���Ͽ��� �о�� �� words ���Ϳ� ����
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				da1 = new DropArea();
				add(da1, BorderLayout.CENTER);
				while(!Running){//�����带 ��� �����. 
					try {
						t.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			if(speed!=0)
			{
				if(time % (40) == 0)//�Ѵܾ ������ �ð�
				{
					viewingWords.add(new Word());
				}
			}
			if(score >=300&&score<1000&&NFirst)//to Normal
			{
				speed=2;
				splitPane.setBottomComponent(imageLabel1);
				NFirst=false;
			}
			if(score >=1000&&score<1300&&HFirst)//to Hard
			{
				speed=4;
				splitPane.setBottomComponent(imageLabel1);
				HFirst=false;
			}
			if(score >=1300)//to Extreme
			{
				if(EFirst)
				{
					speed=6;
					EFirst=false;
					splitPane.setBottomComponent(imageLabel1);
				}
				twinkle++;
			}   
			repaint(); 
		} 
	}//end run

	class DropArea extends JComponent
	{
		ImageIcon backgroundImg = new ImageIcon("cat5.jpg");

		public void paint(Graphics g)
		{ 
			if(Running)
			{
				if(score >=300&&score<1000&&NFirst)//to Normal
				{
					backgroundImg = new ImageIcon("cat2.jpg");
				}
				if(score >=1000&&score<1300&&HFirst)//to Hard
				{
					backgroundImg = new ImageIcon("cat3.jpg");
				}
				if(score >=1300)//to Extreme
				{
					backgroundImg = new ImageIcon("cat4.jpg");
				}
				g.drawImage(backgroundImg.getImage(), 0, 0, null);
				g.setColor(Color.BLACK);
				g.drawString("life "+heart(life), 10, 10);//10,10 ��ġ
				Integer wrap = new Integer(score);
				g.drawString(wrap.toString(),300,10);//���� ���� ǥ��
				g.drawString(difficulty(score),150,10);//���� ���̵� ǥ��

				for(Word w: viewingWords)
				{
					if(twinkle%20==0)//���� �Ÿ����� ����
						g.setColor(Color.BLACK);
					else
						g.setColor(Color.WHITE);
					g.drawString(w.str, w.x, w.y);
				}//end for
				//set
			}
		}//end paint

		public String heart(int life)// life ����ϱ� ���ؼ� 
		{   String str="";
		for(int i=0; i<life;i++)
		{   str+="��";}
		return str;
		}

		public String difficulty(int score)
		{
			if (score<300)// ȭ���߰��� ���̵� ���
				return "[EASY]";
			else if(score>=300&&score<1000)
				return "[NORMAL]";
			else if(score>=1000&&score<13000)
				return "[HARD]";
			else if(score>=13000)
				return "[EXTREME]";
			else 
				return "";
		}
	}//end DropArea

	class Word
	{
		public int x;//x�� ��ġ
		public int y;//y�� ��ġ
		String str;

		Word()
		{
			x = (int) (Math.random() * WIDTH - 30);
			y = 0;
			str = words.get((int)(Math.random() * words.size()));
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		new AcidRainGame();
	}//end main
}