package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

class Map extends JPanel implements Runnable, KeyListener {
   
   private MainFrame mf;
   private Map m;
   //private NewPage np;
   private PInfoPage pip;
   private UserMenuPage ump;
   private Market market;//SM_추가
   private int movementSP = 5;
   
   boolean keyUp = false;
   boolean keyDown = false;
   boolean keyLeft = false;
   boolean keyRight = false; 
   boolean playerMove = false;

   Toolkit tk = Toolkit.getDefaultToolkit();

   Image map = new ImageIcon("images/gym.PNG").getImage();
   Image map1 = new ImageIcon("images/main.PNG").getImage();
   Image img = new ImageIcon("images/img.PNG").getImage();
   Image lab = new ImageIcon("images/lab.png").getImage();
   Image center = new ImageIcon("images/Center.png").getImage();
   Image pvp = new ImageIcon("images/Pvp.png").getImage();//SM_추가//유저 간 pvp대기실

   //위에 이미지 이름이 바로 rpg.png입니다. 이미지를 불러옵니다
   Image buffimg;// 더블버퍼링용 입니다.
   Graphics gc;

   Thread th;

   int x, y; // 케릭터의 현재 좌표를 받을 변수
   int cnt; //무한 루프를 카운터 하기 위한 변수
   int moveStatus; //케릭터가 어디를 바라보는지 방향을 받을 변수
   int num = 0;
   boolean onOff;

   int escCtn=0;//SM_추가
   public Map(MainFrame mf) {
      
      System.out.println("맵 클래스 실행...");
      
      this.mf = mf;
      this.m = this;
      this.ump = new UserMenuPage(mf, m);
      this.market=new Market(mf,m);//SM_추가
      
      
      //this.pip = new PInfoPage(mf,m);
      //np = new NewPage(mf, m);
      
      onOff = true;
      
      this.setVisible(true);
      this.setSize(1024,768);
      this.setBounds(0,0,1024,768);
      init();
      start();
      
      Dimension screen = tk.getScreenSize();

      int xpos = (int)(screen.getWidth() / 2 - getWidth() / 2);
      int ypos = (int)(screen.getHeight() / 2 - getHeight() / 2);
      setLocation(xpos, ypos);
      
      mf.add(this);
      
   }

   public void init(){
      x = 500;
      y = 600;

      moveStatus = 2;
      //케릭터가 시작할때 바라보는 방향은 아래쪽입니다.
      // 0 : 위쪽, 1 : 오른쪽, 2 : 아래쪽, 3 : 왼쪽

   }

   public void start(){ // 기본적인 명령처리
      System.out.println("스타트");
      mf.addKeyListener(this);
      th = new Thread(this);
      th.start();
   }

   public void run(){ // 스레드 메소드, 무한 루프
      while(true){
         try{
            keyProcess();
            repaint();

            Thread.sleep(20);
            cnt++;
            
            if(!m.isVisible()) {
               while(this.isVisible() == false) {
                  th.wait();
               }
            }
            
            
         }catch(Exception e){
            return;
         }
      }
      
   }

   public void paint(Graphics g) { //더블버퍼링을 사용합니다.
      buffimg = createImage(1024, 768);
      gc = buffimg.getGraphics();
      update(g);
   }

   public void update(Graphics g) {
      //더블 버퍼링을 이용해 버퍼에 그려진것을 가져옵니다.
      DrawImg();
      g.drawImage(buffimg, 0, 0, this);
   }
   
   public void DrawImg() {
      gc.setFont(new Font("Default", Font.BOLD, 20));
      gc.drawString(Integer.toString(cnt), 50, 50);
      gc.drawString(Integer.toString((playerMove)?1:0),200, 50);

      switch(num) {
      case 0 : gc.drawImage(map1, 0, 0, 1024, 768, this); break;
      case 1 : gc.drawImage(center, 0, 0, 1024, 768, this); break;
      case 2 : gc.drawImage(pvp, 0, 0, 1024, 768, this); break;//SM_추가
      case 3 : gc.drawImage(lab, 0, 0, 1024, 768, this); break;
      case 4 : gc.drawImage(map, 0, 0, 1024, 768, this); break;
      }

      //위는 단순히 무한루프 적용여부와 케릭터 방향 체크를 위해
      //눈으로 보면서 테스트할 용도로 쓰이는 텍스트 표출입니다.

      MoveImage(img, x, y, 32, 32);
      //케릭터를 걸어가게 만들기 위해 추가로 만든 메소드 입니다.
   }

   public void MoveImage(Image img, int x, int y, int width, int height) {
      //케릭터 이미지, 케릭터 위치, 케릭터 크기를 받습니다.
      //받은 값을 이용해서 위의 이미지칩셋에서 케릭터를 잘라내
      //표출하도록 계산하는 메소드 입니다.

      gc.setClip(x  , y, width, height);
      //현재 좌표에서 케릭터의 크기 만큼 이미지를 잘라 그립니다.

      if( playerMove ){ // 케릭터의 움직임 여부를 판단합니다.
         if ( cnt / 10 % 4 == 0 ){ gc.drawImage(img,
               x - ( width * 0 ), y - ( height * moveStatus ), this);

         }else  if(cnt/10%4 == 1){ gc.drawImage(img,
               x - ( width * 1 ), y - ( height * moveStatus ), this);

         }else  if(cnt/10%4 == 2){  gc.drawImage(img,
               x - ( width * 2 ), y - ( height * moveStatus ), this);

         }else  if(cnt/10%4 == 3){ gc.drawImage(img,
               x - ( width * 1 ), y - ( height * moveStatus ), this);
         }
         //케릭터의 방향에 따라 걸어가는 모션을 취하는 
         //케릭터 이미지를 시간차를 이용해 순차적으로 그립니다.

      }else {gc.drawImage(img, x - ( width * 1 ), 
            y - ( height * moveStatus ), this); 
      //케릭터가 움직이지 않으면 정지한 케릭터를 그립니다.
      }
   }

   public void keyProcess(){
      //여기서는 단순 케릭터가 이동하는 좌표 말고도
      //케릭터의 움직임 여부및 방향을 체크 합니다.
      playerMove = false;

      if ( keyUp && keyDown == false){
         playerMove = true;
         y -= movementSP;
         moveStatus = 3;
      }

      if ( keyDown){
         y += movementSP;
         moveStatus = 0;
         playerMove = true;
      }

      if ( keyLeft && keyDown == false && keyUp == false){
         x -= movementSP;
         moveStatus = 1;
         playerMove = true;
      }

      if ( keyRight && x < 780 && keyDown == false && keyUp == false){
         x += movementSP;
         moveStatus = 2;
         playerMove = true;

      }
   }

   public void keyPressed(KeyEvent e) {
      if(escCtn==0&&e.getKeyCode() == 27) { //SM_추가//기존꺼는 Market에서 오류 발생
         System.out.println("esc 누름 = 유저메뉴");
         
         m.setVisible(false);
         mf.add(ump);
         ump.setVisible(true);
         run();
      }
      /*//SM_추가 좌표 확인용
       * if(e.getKeyCode()==32) {
    	  System.out.println("x : "+x+"y : "+y);
      }*/


      switch(e.getKeyCode()){
      case KeyEvent.VK_LEFT :
         keyLeft = true;
         break;
      case KeyEvent.VK_RIGHT :
         keyRight = true;
         break;
      case KeyEvent.VK_UP :
         keyUp = true;
         break;
      case KeyEvent.VK_DOWN :
         keyDown = true;
         break;
      case KeyEvent.VK_D : 
         System.out.println("x : " + x + " y : " + y + " num : " + num);
         break;
      }
      
      //체육관
      if( num == 4 && (x > 390 && x<440) &&
            (y>670)) {
         num =0;
         x= 488;
         y = 150;
      }

      //마을_체육관입
      if( num ==0 &&(x >480 && x <510) &&
            (y<148)) {

         num =4;
         x = 430;
         y=670;
      }
      //연구소
      if( num == 3 && (x > 500 && x<550) &&
            (y>670)) {
         num =0;
         x= 180;
         y = 140;
      }
      //마을_연구소입
      if( num ==0 &&(x > 170 && x < 200) && (y<130)){
      num =3;
      x = 525;
      y=670;
      }
      
    //SM_추가
      //센터
      if(num==1) {
         //센터에서 다시 마을로
         if((x > 450 && x<500) &&(y>670)) {
            num =0;
            x= 765;
            y = 610;
         }
         //센터에서 pvp대기실로
         if((x>415 &&x<520)&&(y<10)) {
            num=2;
            x=480;
            y=650;
         }
         //센터에서 상점 이용하기
         if((x>190&&x<250)&&(y>350&&y<375)){
        	 escCtn=1;
            System.out.println("상점 이용");
            //this.market = new Market(mf,m);
            m.setVisible(false);
            mf.add(market);
            market.setVisible(true);
            run();
            
            x=230;
            y=400;
            
            //ChangePanel.changePanel(mf, this, new Market(mf,m));
         }
      }
      //pvp대기실에서 센터로
      if(num==2) {
         if((x>450&&x<500)&&(y>670)){
            num=1;
            x=480;
            y=30;
         }
      }
      
      
      //마을_센터입
      if( num == 0 && (x > 750 && x< 780) &&
            (y<600 && y>550)) {
         num =1;
         x= 475;
         y = 670;
      }
      
      
      

      

   }
   
 //SM_추가
   //escCtn 필드 때문에 추가 Market에서 escCtn값을 변경하기 위해 사용   
   public int getEscCtn() {
	return escCtn;
}

public void setEscCtn(int escCtn) {
	this.escCtn = escCtn;
}

public void keyReleased(KeyEvent e) {
      switch(e.getKeyCode()){
      case KeyEvent.VK_LEFT :
         keyLeft = false;
         break;
      case KeyEvent.VK_RIGHT :
         keyRight = false;
         break;
      case KeyEvent.VK_UP :
         keyUp = false;
         break;
      case KeyEvent.VK_DOWN :
         keyDown = false;
         break;
      }
   }

   public void keyTyped(KeyEvent e) {}

}