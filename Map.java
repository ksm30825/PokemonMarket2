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
   private Market market;//SM_�߰�
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
   Image pvp = new ImageIcon("images/Pvp.png").getImage();//SM_�߰�//���� �� pvp����

   //���� �̹��� �̸��� �ٷ� rpg.png�Դϴ�. �̹����� �ҷ��ɴϴ�
   Image buffimg;// ������۸��� �Դϴ�.
   Graphics gc;

   Thread th;

   int x, y; // �ɸ����� ���� ��ǥ�� ���� ����
   int cnt; //���� ������ ī���� �ϱ� ���� ����
   int moveStatus; //�ɸ��Ͱ� ��� �ٶ󺸴��� ������ ���� ����
   int num = 0;
   boolean onOff;

   int escCtn=0;//SM_�߰�
   public Map(MainFrame mf) {
      
      System.out.println("�� Ŭ���� ����...");
      
      this.mf = mf;
      this.m = this;
      this.ump = new UserMenuPage(mf, m);
      this.market=new Market(mf,m);//SM_�߰�
      
      
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
      //�ɸ��Ͱ� �����Ҷ� �ٶ󺸴� ������ �Ʒ����Դϴ�.
      // 0 : ����, 1 : ������, 2 : �Ʒ���, 3 : ����

   }

   public void start(){ // �⺻���� ���ó��
      System.out.println("��ŸƮ");
      mf.addKeyListener(this);
      th = new Thread(this);
      th.start();
   }

   public void run(){ // ������ �޼ҵ�, ���� ����
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

   public void paint(Graphics g) { //������۸��� ����մϴ�.
      buffimg = createImage(1024, 768);
      gc = buffimg.getGraphics();
      update(g);
   }

   public void update(Graphics g) {
      //���� ���۸��� �̿��� ���ۿ� �׷������� �����ɴϴ�.
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
      case 2 : gc.drawImage(pvp, 0, 0, 1024, 768, this); break;//SM_�߰�
      case 3 : gc.drawImage(lab, 0, 0, 1024, 768, this); break;
      case 4 : gc.drawImage(map, 0, 0, 1024, 768, this); break;
      }

      //���� �ܼ��� ���ѷ��� ���뿩�ο� �ɸ��� ���� üũ�� ����
      //������ ���鼭 �׽�Ʈ�� �뵵�� ���̴� �ؽ�Ʈ ǥ���Դϴ�.

      MoveImage(img, x, y, 32, 32);
      //�ɸ��͸� �ɾ�� ����� ���� �߰��� ���� �޼ҵ� �Դϴ�.
   }

   public void MoveImage(Image img, int x, int y, int width, int height) {
      //�ɸ��� �̹���, �ɸ��� ��ġ, �ɸ��� ũ�⸦ �޽��ϴ�.
      //���� ���� �̿��ؼ� ���� �̹���Ĩ�¿��� �ɸ��͸� �߶�
      //ǥ���ϵ��� ����ϴ� �޼ҵ� �Դϴ�.

      gc.setClip(x  , y, width, height);
      //���� ��ǥ���� �ɸ����� ũ�� ��ŭ �̹����� �߶� �׸��ϴ�.

      if( playerMove ){ // �ɸ����� ������ ���θ� �Ǵ��մϴ�.
         if ( cnt / 10 % 4 == 0 ){ gc.drawImage(img,
               x - ( width * 0 ), y - ( height * moveStatus ), this);

         }else  if(cnt/10%4 == 1){ gc.drawImage(img,
               x - ( width * 1 ), y - ( height * moveStatus ), this);

         }else  if(cnt/10%4 == 2){  gc.drawImage(img,
               x - ( width * 2 ), y - ( height * moveStatus ), this);

         }else  if(cnt/10%4 == 3){ gc.drawImage(img,
               x - ( width * 1 ), y - ( height * moveStatus ), this);
         }
         //�ɸ����� ���⿡ ���� �ɾ�� ����� ���ϴ� 
         //�ɸ��� �̹����� �ð����� �̿��� ���������� �׸��ϴ�.

      }else {gc.drawImage(img, x - ( width * 1 ), 
            y - ( height * moveStatus ), this); 
      //�ɸ��Ͱ� �������� ������ ������ �ɸ��͸� �׸��ϴ�.
      }
   }

   public void keyProcess(){
      //���⼭�� �ܼ� �ɸ��Ͱ� �̵��ϴ� ��ǥ ����
      //�ɸ����� ������ ���ι� ������ üũ �մϴ�.
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
      if(escCtn==0&&e.getKeyCode() == 27) { //SM_�߰�//�������� Market���� ���� �߻�
         System.out.println("esc ���� = �����޴�");
         
         m.setVisible(false);
         mf.add(ump);
         ump.setVisible(true);
         run();
      }
      /*//SM_�߰� ��ǥ Ȯ�ο�
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
      
      //ü����
      if( num == 4 && (x > 390 && x<440) &&
            (y>670)) {
         num =0;
         x= 488;
         y = 150;
      }

      //����_ü������
      if( num ==0 &&(x >480 && x <510) &&
            (y<148)) {

         num =4;
         x = 430;
         y=670;
      }
      //������
      if( num == 3 && (x > 500 && x<550) &&
            (y>670)) {
         num =0;
         x= 180;
         y = 140;
      }
      //����_��������
      if( num ==0 &&(x > 170 && x < 200) && (y<130)){
      num =3;
      x = 525;
      y=670;
      }
      
    //SM_�߰�
      //����
      if(num==1) {
         //���Ϳ��� �ٽ� ������
         if((x > 450 && x<500) &&(y>670)) {
            num =0;
            x= 765;
            y = 610;
         }
         //���Ϳ��� pvp���Ƿ�
         if((x>415 &&x<520)&&(y<10)) {
            num=2;
            x=480;
            y=650;
         }
         //���Ϳ��� ���� �̿��ϱ�
         if((x>190&&x<250)&&(y>350&&y<375)){
        	 escCtn=1;
            System.out.println("���� �̿�");
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
      //pvp���ǿ��� ���ͷ�
      if(num==2) {
         if((x>450&&x<500)&&(y>670)){
            num=1;
            x=480;
            y=30;
         }
      }
      
      
      //����_������
      if( num == 0 && (x > 750 && x< 780) &&
            (y<600 && y>550)) {
         num =1;
         x= 475;
         y = 670;
      }
      
      
      

      

   }
   
 //SM_�߰�
   //escCtn �ʵ� ������ �߰� Market���� escCtn���� �����ϱ� ���� ���   
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