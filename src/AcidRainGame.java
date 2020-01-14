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
	int life = 5;//생명갯수
	int score=0;//score 저장하는곳
	int speed = 1;//속도 조절
	int twinkle =0;// extreme 깜빡이게 하는것
	boolean Running =false;//게임 실행중인지
	boolean First = true;//맨처음 일때 구별
	boolean NFirst = true;//맨처음 노말들어갈때만 속도 설정
	boolean HFirst = true;//맨처음 하드 들어갈때만 속도
	boolean EFirst = true;// 같음
	String Ranking ="";//랭킹에 들어가는 내용
	Vector<String> words;//단어
	Vector<Word> viewingWords;//내려오는 단어들 
	BufferedReader inputStream;//파일 이름
	Thread t;
	long time;
	int tempspeed=1;

	DropArea da1;//단어가 떨어지고 있는구간
	JTextField t1;//텍스트 박스 입력하는곳
	JPanel buttons;// start howtoplay rangking 있는 패널
	JSplitPane splitPane;// 패널을 그림있는 구간과 버튼 구간으로 나눔
	JLabel imageLabel1,imageLabel2,imageLabel3,imageLabel4;
	JPanel Pause;//일지정지 화면 구현
	JLabel PLabel;

	void createMenu(){
		JMenuBar mb = new JMenuBar();
		//setting 메뉴
		JMenuItem [] menuItem =new JMenuItem[3];
		String[] itemTitle = {"제작자","만든계기", "만든소감"};
		JMenu settingMenu = new JMenu("Setting");

		for(int i=0;i<menuItem.length;i++){
			menuItem[i] =new JMenuItem(itemTitle[i]);
			menuItem[i].addActionListener(new MenuActionListener());

			settingMenu.add(menuItem[i]);
		}
		//run 메뉴
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
			if(cmd.equals("제작자")){
				JOptionPane.showMessageDialog(null, "mdae by 서은정,함지현","제작자",JOptionPane.CLOSED_OPTION);
			}
			else if(cmd.equals("만든계기")){
				JOptionPane.showMessageDialog(null, "자바의 Swing,Thread 등으로 만들 수 있는 게임을 구현해보고자 만들게되었다."
						,"만든계기",JOptionPane.CLOSED_OPTION);
			}
			else {
				JOptionPane.showMessageDialog(null, "서은정: 처음에 타자게임을 만들려고 하니까 아는게 부족해서 막막했었다.\n"
						+ "그래도 친구와 서로 으쌰으쌰 해서 만들어내니 뿌듯하다.\n "
						+ "만들어내지 못할 것만 했던 프로젝트가 실행이 되니 기분이 좋았고,\n"
						+ "이 프로젝트를 통해 좀 더 자바를 많이 배울 수 있게 된것 같았다.\n\n"
						+ "함지현: 항상 느끼는 것이지만 프로젝트를 할때마다 그 과정이 너무나 어렵고 힘들다\n"
						+ "그러나 내가 짠 코드가 실행이 되고 무언가를 이뤄낼때 그 뿌듯함이 너무나 좋다. 이것 또한 그랬다.\n"
						+ "어렵고 힘들었던 과정이지만 하나하나가 다 나에게 도움이 되는 순간이었다.\n"
						+ "아쉬움이 남는 프로젝트였지만 하는 동안에는 즐거움 마음으로 임할 수 있었다.\n"
						+ "프로젝트를 지도해주신 유상미교수님께 감사의 말씀을 드린다. ","만든소감",JOptionPane.CLOSED_OPTION);
			}
		}
	}
	class MenuActionListener1 implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String cmd = e.getActionCommand();
			if(cmd.equals("esc")){
				Pause = new JPanel();//일지정지 화면 구현
				BorderLayout A= new BorderLayout();
				Pause.setLayout(A);
				PLabel= new JLabel("PAUSE",JLabel.CENTER);

				Running=false;
				da1.setVisible(false);
				splitPane.setVisible(false);
				t1.setVisible(false);//다른 것들 안보이게
				Pause.setVisible(true);//일시정시 화면 보이도록

				// 일시정지 화면 이제 추가해줌
				tempspeed=speed;
				speed=0;
				add(Pause,BorderLayout.CENTER);
				Pause.add("North",PLabel);

			}
			else{
				da1.setVisible(true);
				splitPane.setVisible(true);
				t1.setVisible(true);
				Running=true;//실행중으로 바꿈
				Pause.setVisible(false);//pause 화면은 다시 안보이도록함
				speed=tempspeed;
				Running=true;
			}
		}
	}

	public AcidRainGame() throws IOException, InterruptedException {//생성자
		// 단어 목록 읽기
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
			inputStream = new BufferedReader(new FileReader("words.txt"));//단어리스트 파일들
		} catch (FileNotFoundException e) {//파일 없을때 예외처리
			e.printStackTrace();
		}
		buttons = new JPanel();
		
		addButton(buttons, "Start",new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if(First)
				{t.start();First=false;}//시작하면 쓰레드가 시작됨
				Running=true;// 실행중으로
			}
		});
		addButton(buttons, "How to Play",new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(null, "게임방법\n1.떨어오는 단어를 하단에 있는 입력창에서 입력한 후 ENTER\n"
						+ "2.알맞은 단어를 입력하면 단어가 없어지고 50점이 올라갑니다\n"
						+ "3.입력하지 못한 단어가 밑으로 떨어지면 목숨이 줄어듭니다\n"
						+ "4.LEVEL이 올라갈수록 속도가 빨라집니다\n"
						+ "즐거운 게임되세요^_^");
			}
		});
		addButton(buttons, "Ranking",new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				JOptionPane.showMessageDialog(null, Ranking);
			}//여기에 랭킹 들이 추가 된다.
		});
		words = new Vector<String>();//파일에서 읽어온 단어들 저장
		viewingWords = new Vector<Word>();//내려오고있는 단어들
		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);//JSplitPane로 나눔
		splitPane.setTopComponent(buttons);//위에는 버튼이 있다.
		imageLabel1 = new JLabel(new ImageIcon("cat1.jpg"));
		imageLabel2 = new JLabel(new ImageIcon("cat2.jpg"));
		imageLabel3 = new JLabel(new ImageIcon("cat3.jpg"));
		imageLabel4 = new JLabel(new ImageIcon("cat4.jpg"));

		String w;
		while((w = inputStream.readLine()) != null)
		{
			words.add(w);//파일에서 읽어온 후 words 벡터에 넣음
		}
		da1 = new DropArea();//떨어지는 단어들의 공간
		da1.setPreferredSize(new Dimension(WIDTH, HEIGHT));//크기

		add(da1, BorderLayout.CENTER);//프레임 가운데에 추가
		t1 = new JTextField(20);//입력부
		t1.addActionListener(this);
		Pause = new JPanel();//일지정지 화면 구현
		BorderLayout A= new BorderLayout();

		Pause.setLayout(A);

		PLabel= new JLabel("PAUSE",JLabel.CENTER);

		JButton resume = new JButton("RESUME");//이버튼 누르면 다시 게임으로 돌아간다.
		Pause.add("North",PLabel);
		Pause.add("Center",resume);

		resume.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//다시 다른 것들을 보이도록 만든다
				da1.setVisible(true);
				splitPane.setVisible(true);
				t1.setVisible(true);
				Running=true;//실행중으로 바꿈
				Pause.setVisible(false);//pause 화면은 다시 안보이도록함
				speed=tempspeed;
				Running=true;
			}

		});
		Pause.setVisible(false);//pause 는 처음엔 안보이도록 설정한다.

		t1.addKeyListener(new KeyAdapter(){//텍스트 박스에서 엔터키 누를 때 이벤트
			public void keyPressed(KeyEvent e){
				if(Running)
					// 키가 눌렷을때의 이벤트
				{
					String s = e.getKeyText(e.getKeyCode()); // 키값
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE){ // ESC 누를시
						Running=false;

						da1.setVisible(false);
						splitPane.setVisible(false);
						t1.setVisible(false);//다른 것들 안보이게
						Pause.setVisible(true);//일시정시 화면 보이도록

						// 일시정지 화면 이제 추가해줌
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
			if(t1.getText().equals(w.str))//입력부와 같으면 그때의 index를 찾아낸
			{
				index = viewingWords.indexOf(w);
				score +=50;
			}
		}
		if(index != -1)
		{
			viewingWords.remove(index);//떨어진 단어 remove
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
				t.sleep(100/(speed+1));//속도 조절
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
				if(viewingWords.get(0).y > HEIGHT-10)//떨어졌을때
				{
					life--;
					viewingWords.remove(0);
				}
			}

			if(life <= 0)
			{
				Integer wrap = new Integer(score);
				Ranking+= JOptionPane.showInputDialog(score+"점 - 이름을 입력하세요");
				Ranking+=" " + wrap.toString()+"점\n";
				//게임이 끝났으므로 다음 게임을 위해서 다시 초기화 해준다.
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
					inputStream = new BufferedReader(new FileReader("words.txt"));//단어리스트 파일들
				} catch (FileNotFoundException e) {//파일 없을때 예외처리
					e.printStackTrace();
				}
				String w;
				try {
					while((w = inputStream.readLine()) != null)
					{
						words.add(w);//파일에서 읽어온 후 words 벡터에 넣음
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				da1 = new DropArea();
				add(da1, BorderLayout.CENTER);
				while(!Running){//쓰레드를 잠시 멈춘다. 
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
				if(time % (40) == 0)//한단어가 나오는 시간
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
				g.drawString("life "+heart(life), 10, 10);//10,10 위치
				Integer wrap = new Integer(score);
				g.drawString(wrap.toString(),300,10);//현재 점수 표시
				g.drawString(difficulty(score),150,10);//현재 난이도 표시

				for(Word w: viewingWords)
				{
					if(twinkle%20==0)//깜빡 거리도록 만듬
						g.setColor(Color.BLACK);
					else
						g.setColor(Color.WHITE);
					g.drawString(w.str, w.x, w.y);
				}//end for
				//set
			}
		}//end paint

		public String heart(int life)// life 출력하기 위해서 
		{   String str="";
		for(int i=0; i<life;i++)
		{   str+="●";}
		return str;
		}

		public String difficulty(int score)
		{
			if (score<300)// 화면중간에 난이도 출력
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
		public int x;//x축 위치
		public int y;//y축 위치
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