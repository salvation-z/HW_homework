package huawei;

import huawei.exam.*;

/**
 * 实现类
 * 
 * 各方法请按要求返回，考试框架负责报文输出
 */

public class DragonSlayerImpl implements ExamOp
{
    /**
     * ReturnCode(返回码枚举) .S001：重置成功 .S002：设置火焰成功 .S003：设置龙卷风成功 .S004：设置传送阵成功 .E001：非法命令
     * .E002：非法坐标 .E003：非法时间 .E004：操作时间不能小于系统时间 .E005：该区域不能设置元素 .E006：龙卷风数量已达上限
     * .E007：传送阵数量已达上限 .E008：传送阵的入口和出口重叠
     */    
    /**
     * Area(区域类) int getX()：获取横坐标 void setX(int x)：设置横坐标 int getY()：获取纵坐标 void setY(int
     * y)：设置纵坐标 Element getElement()：获取元素 void setElement(Element element)：设置元素 boolean
     * equals(Object o)：区域横纵坐标相同，则区域相同
     */  
    /**
     * Element(元素枚举) .NONE：空元素 .HERO：英雄 .DRAGON：恶龙 .FIRE：火焰 .TORNADO：龙卷风 .PORTAL：传送阵
     */  
    /**
     * Hero(英雄类) Title getTitle()：获取称号 void setTitle(Title title)：设置称号 Status
     * getStatus()：获取状态 void setStatus(Status status)：设置状态 Area getArea()：获取区域
     * setArea(Area area)：设置区域
     */
    /**
     * Title(称号枚举) .WARRIOR：勇士 .DRAGON_SLAYER：屠龙者
     */
    /**
     * Status(状态枚举) .MARCHING：行进 .WAITING：等待
     */
	private Area[][] map;
    private Hero hero;
    private int current_time;
    private int Tornado_num;
    private int Portal_num;
    private Area portal_exit;
    private Area portal_entry;
    private Area[] current_path;
    /**
     * 待考生实现，构造函数
     */
    public DragonSlayerImpl()
    {
    	this.Tornado_num=0;
    	this.Portal_num=0;
    	this.current_path=new Area[256];
    	//Area h_area=new Area(0,0);
    	this.map=new Area[16][16];
    	for(int i=0;i<16;i++)
    		for(int j=0;j<16;j++) {
    			this.map[i][j]=new Area(i,j);
    			this.map[i][j].setElement(Element.NONE);
    		}
    	this.hero=new Hero(Title.WARRIOR,Status.MARCHING,map[0][0]);
    	for(int i=0;i<256;i++) {
    		if(i<16) {
    			this.current_path[i]=map[i][i];
    		}
    		else {
    			this.current_path[i]=map[15][15];
    		}
    	}
    	update_H(0);
    	update();
    }
  
    /**
     * 待考生实现，系统重置
     * 
     * @return 返回码
     */
    @Override
    public OpResult reset()
    {
    	this.Tornado_num=0;
    	this.Portal_num=0;
    	//this.hero.setStatus(Status.WAITING);
    	//this.hero.setTitle(Title.WARRIOR);
    	for(int i=0;i<16;i++)
    		for(int j=0;j<16;j++) {
    			this.map[i][j].setX(i);
    			this.map[i][j].setY(j);
    			this.map[i][j].setElement(Element.NONE);
    		}
    	for(int i=0;i<256;i++) {
    		if(i<16) {
    			this.current_path[i]=map[i][i];
    		}
    		else {
    			this.current_path[i]=map[15][15];
    		}
    	}
    	update_H(0);
    	update();
        return new OpResult(ReturnCode.S001);
    }
    
    /**
     * 待考生实现，设置火焰
     * 
     * @param area 设置区域
     * @param time 设置时间
     * @return 返回码
     */
    @Override
    public OpResult setFire(Area area, int time)
    {
    	int x=area.getX();
    	int y=area.getY();
    	if(time<current_time)
			return new OpResult(ReturnCode.E004);
    	if((!this.map[x][y].getElement().equals(Element.NONE))||(this.current_path[time].equals(area))||((x*16+y)==255)) {
    		return new OpResult(ReturnCode.E005);
    	}
			
    	update_H(time);
    	this.map[x][y].setElement(Element.FIRE);
    	update();
    	return new OpResult(ReturnCode.S002);
        //return new OpResult(ReturnCode.E001);
    }
    
    /**
     * 待考生实现，设置龙卷风
     * 
     * @param area 设置区域
     * @param time 设置时间
     * @return 返回码
     */
    @Override
    public OpResult setTornado(Area area, int time)
    {
    	int x=area.getX();
    	int y=area.getY();
    	if(time<this.current_time)
    		return new OpResult(ReturnCode.E004); 
    	if((!this.map[x][y].getElement().equals(Element.NONE))||(this.current_path[time].equals(area))||((x*16+y)==255))
    		return new OpResult(ReturnCode.E005);
    	if(this.Tornado_num>=1)
    		return new OpResult(ReturnCode.E006);
    	update_H(time);
    	this.map[x][y].setElement(Element.TORNADO);
    	this.Tornado_num=1;
    	update();
    	return new OpResult(ReturnCode.S003);
        //return new OpResult(ReturnCode.E001);
    }
    
    /**
     * 待考生实现，设置传送阵
     * 
     * @param entry 入口区域
     * @param exit 出口区域
     * @param time 设置时间
     * @return 返回码
     */
    @Override
    public OpResult setPortal(Area entry, Area exit, int time)
    {
    	int x=entry.getX();
    	int y=entry.getY();
    	if(time<this.current_time)
    		return new OpResult(ReturnCode.E004);
    	if((!this.map[x][y].getElement().equals(Element.NONE))||(this.current_path[time].equals(entry))||((x*16+y)==255))
			return new OpResult(ReturnCode.E005);
    	if(this.Portal_num>=1)
			return new OpResult(ReturnCode.E007);
    	if(exit.equals(entry))
    		return new OpResult(ReturnCode.E008);
    	update_H(time);
    	this.map[x][y].setElement(Element.PORTAL);
    	this.Portal_num=1;
    	this.portal_exit=this.map[exit.getX()][exit.getY()];
    	this.portal_entry=this.map[x][y];
    	update();
    	return new OpResult(ReturnCode.S004);
        //return new OpResult(ReturnCode.E001);
    }
    
    /**
     * 待考生实现，查询
     * 
     * @param time 查询时间
     * @return 英雄信息
     */
    @Override
    public OpResult query(int time)
    {
    	if(time<this.current_time)
    		return new OpResult(ReturnCode.E004);
    	update_H(time);
    	return new OpResult(this.hero);
        //return new OpResult(ReturnCode.E001);
    }
    
    //update used to update Status、path(according to map)
    private void update() 
    {
    	//当前位于终点
    	if(this.hero.getArea().equals(map[15][15])) 
    	{
    		this.hero.setStatus(Status.WAITING);
    		return;
    	}
		//当前不位于终点
        int[][] value=new int[256][256];//邻接矩阵
        for(int i=0;i<256;i++) {
			for(int j=0;j<256;j++) {
				if((i==j+1)||(i==j-1)||(i==j+16)||(i==j-16)||(i==j-15)||(i==j+15)||(i==j-17)||(i==j+17)) {
					if((map[i/16][i%16].getElement()==Element.FIRE)||(map[j/16][j%16].getElement()==Element.FIRE))//任意一个是火焰则不可联通
					{
						value[i][j]=-1;
					}	
					else
						if((map[i/16][i%16].getElement()==Element.TORNADO))//离开龙卷风区域需要三倍时间(权)
						{
							value[i][j]=3;
						}
						else {
							value[i][j]=1;
						}
				}
				else {
						value[i][j]=-1;
				}
			}
			//边界处理
			if(i%16==15) {
				if(i+1<=255)
					value[i][i+1]=-1;
				if(i+17<=255)
					value[i][i+17]=-1;
				if(i>=15)
					value[i][i-15]=-1;
				if(i+1<=255)
					value[i+1][i]=-1;
				if(i+17<=255)
					value[i+17][i]=-1;
				if(i>=15)
					value[i-15][i]=-1;
			}
			if(i%16==0) {
				if(i>=1)
					value[i][i-1]=-1;
				if(i>=17)
					value[i][i-17]=-1;
				if(i+15<=255)
					value[i][i+15]=-1;
				if(i>=1)
					value[i-1][i]=-1;
				if(i>=17)
					value[i-17][i]=-1;
				if(i+15<=255)
					value[i+15][i]=-1;
			}
        }
        if(this.Portal_num>0) 
        {
        	int entry=this.portal_entry.getX()*16+this.portal_entry.getY();
        	int exit=this.portal_exit.getX()*16+this.portal_exit.getY();
        	if(entry>=1)
        		value[entry-1][entry]=-1;
        	if(entry<=254)
        		value[entry+1][entry]=-1;
        	if(entry>=16)
        		value[entry-16][entry]=-1;
        	if(entry<=239)
        		value[entry+16][entry]=-1;
        	if(entry>=15)
        		value[entry-15][entry]=-1;
        	if(entry<=240)
        		value[entry+15][entry]=-1;
        	if(entry>=17)
        		value[entry-17][entry]=-1;
        	if(entry<=238)
        		value[entry+17][entry]=-1;
        	//传送阵直通出口
        	if(map[exit/16][exit%16].getElement()!=Element.FIRE) {
        		if(map[exit/16][exit%16].getElement()==Element.TORNADO) {
        			if(entry>=1)
        				value[entry-1][exit]=3;
        			if(entry<=254)
        				value[entry+1][exit]=3;
        			if(entry>=16)
        				value[entry-16][exit]=3;
        			if(entry<=239)
        				value[entry+16][exit]=3;
        			if(entry>=15)
        				value[entry-15][exit]=3;
        			if(entry<=240)
        				value[entry+15][exit]=3;
        			if(entry>=17)
        				value[entry-17][exit]=3;
        			if(entry<=238)
        				value[entry+17][exit]=3;
        		}
        		else {
        			if(entry>=1)
        				value[entry-1][exit]=1;
        			if(entry<=254)
        				value[entry+1][exit]=1;
        			if(entry>=16)
        				value[entry-16][exit]=1;
        			if(entry<=239)
        				value[entry+16][exit]=1;
        			if(entry>=15)
        				value[entry-15][exit]=1;
        			if(entry<=240)
        				value[entry+15][exit]=1;
        			if(entry>=17)
        				value[entry-17][exit]=1;
        			if(entry<=238)
        				value[entry+17][exit]=1;
        		}	
        	}
		}
		int[] distance;
		Area[] path;
		boolean same;
		System.out.println("dijk");
		distance=dijkstra(value);
		System.out.println("path"+distance[255]);
		if(distance[255]!=-1) {
			path=find_path(distance,value);
			System.out.println("comp");
			same=this.compare(path, this.current_path);
			if(!same) {
				this.current_path=path;
			}
		}
    }
    
    //update_H used to update Title、current_pos(according to time)
    private void update_H(int time) {
    	this.current_time=time;
    	this.hero.setArea(this.current_path[time]);
    	if(this.hero.getArea().equals(map[15][15])) 
    	{
    		this.hero.setTitle(Title.DRAGON_SLAYER);
    		this.hero.setStatus(Status.WAITING);
    		}
    	else
    		this.hero.setTitle(Title.WARRIOR);
    }
    
    private int[] dijkstra(int[][] value) {
    	int pos=this.hero.getArea().getX()*16+this.hero.getArea().getY();
		int[] dis=new int[256];//维护到出发点的已知最短距离
		boolean[] set=new boolean[256];//维护已扩展点集
		for(int i=0;i<256;i++) {
			set[i]=false;
		}
		set[pos]=true;
		dis=value[pos].clone();
		while(set[255]!=true) {
			int min=2147483647;
			int t_pos=-1;
			//找最小权拓展节点
			for(int i=0;i<256;i++) {
				if((dis[i]!=-1)&&(set[i]==false)&&(dis[i]<min)) {
					min=dis[i];
					t_pos=i;
				}
			}
			//更新节点距离
			if(t_pos!=-1) {
				set[t_pos]=true;
				for(int i=0;i<256;i++) {
					if(value[t_pos][i]!=-1) {
						if(value[t_pos][i]+dis[t_pos]<dis[i])
							dis[i]=value[t_pos][i]+dis[t_pos];
						if(dis[i]==-1)
							dis[i]=value[t_pos][i]+dis[t_pos];
					}
				}
			}
			else
				return dis;
		}
		dis[pos]=0;
		return dis;
	}
    
    private Area[] find_path(int[] dis,int[][] value) {
    	int[] path=new int[256];//使用邻接矩阵表示的path
    	Area[] Path=new Area[256];//使用Area表示的path
    	int pos=255;
    	int time=this.current_time+dis[255];
    	path[time]=pos;
    	for(int i=time;i<255;i++)
    		path[i]=255;
		while(pos!=(this.hero.getArea().getX()*16+this.hero.getArea().getY())) {
			boolean find=false;
			int time_t=0;
			int pos_t=0;
			for(int i=0;i<255;i++) {
				if((dis[pos]==dis[i]+value[i][pos])&&(value[i][pos]!=-1)&&(i!=pos)) {
					if(find) {
						//有多条路径
						this.hero.setStatus(Status.WAITING);
						for(int j=this.current_time;j<256;j++)
							Path[j]=this.hero.getArea();
						return Path;
					}
					//继续创建路径
					for(int j=0;j<value[i][pos];j++)
						path[time-j-1]=i;
					pos_t=i;
					time_t=time-value[i][pos];
					find=true;
				}
			}
			pos=pos_t;
			time=time_t;
		}
		this.hero.setStatus(Status.MARCHING);
		for(int i=0;i<256;i++) {
			Path[i]=map[path[i]/16][path[i]%16];
		}
		return Path;
	}
    
    private boolean compare(Area[] path_a,Area[] path_b) {
    	boolean t1,t2,t3;
    	t1=true;
    	t2=true;
    	t3=true;
    	for(int i=this.current_time;i<256;i++) {
//    		if(path_a[i].equals(map[15][15]))
//    			break;
    		if(!path_a[i].equals(path_b[i])) {
    			t1=false;
    			break;
    		}
    	}
    	for(int i=this.current_time;i<255;i++) {
//    		if(path_a[i+1].equals(map[15][15]))
//    			break;
    		if(!path_a[i+1].equals(path_b[i])) {
    			t2=false;
    			break;
    		}
    	}
    	for(int i=this.current_time;i<254;i++) {
//    		if(path_a[i+2].equals(map[15][15]))
//    			break;
    		if(!path_a[i+2].equals(path_b[i])) {
    			t3=false;
    			break;
    		}
    	}
    	return (t1||t2||t3);
    }
    
}
