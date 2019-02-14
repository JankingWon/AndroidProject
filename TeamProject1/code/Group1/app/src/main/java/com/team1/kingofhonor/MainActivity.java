package com.team1.kingofhonor;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.team1.kingofhonor.fragment.Fragment1;
import com.team1.kingofhonor.fragment.Fragment2;
import com.team1.kingofhonor.fragment.Fragment3;
import com.team1.kingofhonor.fragment.Fragment4;
import com.team1.kingofhonor.model.Equipment;
import com.team1.kingofhonor.model.Hero;
import com.team1.kingofhonor.model.Inscription;
import com.team1.kingofhonor.sqlite.HeroSQLiteHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private HeroSQLiteHelper heroSQLiteHelper;
    //背景音乐播放器
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private String[] equip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //背景音乐播放器
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.theme);
        mediaPlayer.start();
        //持续播放音乐
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
        //添加数据库
        heroSQLiteHelper = new HeroSQLiteHelper(this);
        heroSQLiteHelper.deleteAllHeroes();
        //下面用来添加数据
        heroSQLiteHelper.addHero(new Hero("橘右京", "android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.juyoujing, "神梦一刀", "刺客" , 5, 6, 5 ,5,
                "android.resource://com.team1.kingofhonor/" + R.raw.juyoujing,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.juyoujing_icon, true,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.juyoujing1, "秘剑胧刀 冷却值：5消耗：0\n" +
                "\n" +
                "被动：橘右京将下一次普攻将进行一次强力拔刀斩，对前方敌人造成130%物理加成物理伤害并减少50%移动速度，持续2秒；拔刀斩每5秒可准备1次；对处于攻击边缘的敌人将承受50%的额外伤害.",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.juyoujing2, "燕返 冷却值：7消耗：0\n" +
                "\n" +
                "橘右京跃向后方，同时向前方挥刀，对附近的敌人造成200/240/280/320/360/400（+145%物理加成）点物理伤害，若成功命中一名敌方英雄，可激活二段使用",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.juyoujing3, "居合 冷却值：10/9.5/9/8.5/8/7.5消耗：0\n" +
                "\n" +
                "橘右京向指定方向快速拔刀，对路径上的第一个敌人造成330/375/420/465/510/555（+212%物理加成）点物理伤害，对路上的其余敌人造成的伤害将衰减50%,并将路径末端的敌人眩晕0.75秒",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.juyoujing4, "细雪 冷却值：12/11.5/11消耗：0\n" +
                "\n" +
                "橘右京向指定方向连续拔刀四次，每次命中造成100/150/200（+70%物理加成）点物理伤害并且每次命中敌方英雄，自身将回复70/100/130（+35%物理加成）点生命值（命中非英雄单位效果减半）",
                "暗影战斧","抵抗之靴","碎星锤","冰痕之握","破军","名刀·司命"));
        heroSQLiteHelper.addHero(new Hero("李白","android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.libai,"青莲剑仙", "刺客", 4, 7, 6, 9,
                "android.resource://com.team1.kingofhonor/" + R.raw.libai,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.libai_icon, true,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.libai1, "侠客行 冷却值：0消耗：0\n" +
                "\n" +
                "被动：李白使用普通攻击攻击敌人时，会积累1道剑气，持续3秒；积累4道剑气后进入侠客行状态，增加30点物理攻击力并解除青莲剑歌的封印，持续5秒；攻击建筑不会积累剑气",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.libai2, "将进酒 冷却值：12/10.6/9.2/7.8/6.4/5消耗：80\n" +
                "\n" +
                "李白可用醉剑式向指定方向连续突进2次，对路径上的敌人造成300/350/400/450/500/550（+110%物理加成）点物理伤害并且造成晕眩0.5秒；第三次释放会回到原地；每次释放间隔不能超过5秒",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.libai3,"神来之笔 冷却值：12/11.6/11.2/10.8/10.4/10消耗：60\n" +
                "\n" +
                "李白以自身为中心化剑为青莲剑阵，对范围内敌人造成210/246/282/318/354/390（+75%物理加成）点物理伤害；对触碰到剑圈的敌人造成350（+125%物理加成）点物理伤害并且减少90%移动速度，持续1秒；同时敌人会减少100/175/250/325/400/475点物理防御，持续3秒；李白释放技能期间不可被选中",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.libai4,"青莲剑歌 冷却值：12消耗：120\n" +
                "\n" +
                "李白化身为剑气，对指定方向范围内的所有敌人飞速穿梭斩击5次，每次斩击造成250/325/400（+90%物理加成）点物理伤害；当同时攻击多个敌方英雄时，每增加一位敌方英雄将衰减15%的伤害，最低衰减至初始伤害的70%；青莲剑歌需要由侠客行解除限制后方可释放，使用后立即进入限制状态；李白释放技能期间不可被选中和攻击",
                "末世","泣血之刃","抵抗之靴","暗影战斧","破军","破魔刀"));
        heroSQLiteHelper.addHero(new Hero("凯","android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.kai,"破灭刀锋", "战士", 7,7, 4, 2,
                null,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.kai_icon, false,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.kai1, "修罗之魂 冷却值：0消耗：0\n" +
                "\n" +
                "被动：铠拥有精湛的战斗技巧，当铠的普通攻击和极刃风暴只命中了一个目标时将会额外造成50%伤害。",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.kai2, "回旋之刃 冷却值：10消耗：60\n" +
                "\n" +
                "铠向指定方向投掷刀刃，对命中的敌人造成150/180/210/240/270/300（+60%物理加成）点物理伤害并减少其30%移动速度，持续1秒；刀刃命中敌人后会在敌人间弹射，最多4次；命中的第一个目标将会额外减少20%移动速度，持续1秒；当刀刃命中敌人时，铠将回复150/180/210/240/270/300（+60%物理加成）点生命值并且减少30%回旋之刃的冷却时间，同时增加20%移动速度，持续3秒",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.kai3,"极刃风暴 冷却值：6消耗：50\n" +
                "\n" +
                "铠向指定方向发动两次挥砍，每次80/96/112/128/144/160（+30%物理加成）点物理伤害，并且第二次挥砍会将敌人击飞0.5秒；同时使得下一次普通攻击更变为冲砍，冲砍会冲锋至目标身旁并发动普通攻击，冲砍会额外造成80（+30%物理加成）点成物理。被动：脱离战斗后铠每秒回复1%最大生命值及最大法力值并且会增加10/12/14/16/18/20点移动速度",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.kai4,"不灭魔躯 冷却值：60/55/50消耗：120\n" +
                "\n" +
                "铠召唤魔铠，魔铠在1秒后降临，对附近造成300/400/500点法术伤害并强化自身100/150/200攻击力、50点移动速度、60/95/130点伤害格挡；同时会对自身附近的敌人造成60点法术伤害，持续8秒；",
                "暗影战斧","影忍之足","宗师之力","破军","反伤刺甲","暴烈之甲"));
        heroSQLiteHelper.addHero(new Hero("嬴政","android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.yingzheng,"王者独尊", "法师", 3, 4, 9, 6,
                "android.resource://com.team1.kingofhonor/" + R.raw.yingzheng,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.yingzheng_icon, false,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.yingzheng1, "王者审判 冷却值：0消耗：0\n" +
                "\n" +
                "被动：嬴政的普通攻击可以击穿目标，并造成70（+100%物理加成）（+30%法术加成）点法术伤害；攻击机关时会衰减50%的伤害",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.yingzheng2, "王者惩戒 冷却值：8消耗：100\n" +
                "\n" +
                "嬴政在指定目标区域召唤黄金剑阵，持续2.5秒，每0.5秒对范围内的敌人造成100/120/140/160/180/200（+27%法术加成）点法术伤害；范围内敌人会减少10%移动速度，持续1秒，最多叠加5层",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.yingzheng3,"王者守御 冷却值：12/11/10/9/8/7消耗：40\n" +
                "\n" +
                "嬴政开启王之护盾，持续时间内增加自身140/168/196/224/252/280法术攻击力和10/11/12/13/14/15%移动速度；被动：增加嬴政自身70/84/98/112/126/140法术攻击力和2/3/3/4/4/5%移动速度",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.yingzheng4,"至尊王权 冷却值：30消耗：150\n" +
                "\n" +
                "嬴政展示王者权力，增加20%移动速度，持续5秒，期间将会号令55只飞剑持续向指定方向进行冲击，每只飞剑能造成70/90/110（+8%法术加成）点法术伤害，该技能会对小兵和野怪造成25%额外伤害",
                "秘法之靴","博学者之怒","虚无法杖","梦魇之牙","回响之杖","贤者的庇护"));
        heroSQLiteHelper.addHero(new Hero("诸葛亮","android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.zhugeliang,"绝代智谋", "法师", 3, 1, 8, 6,
                "android.resource://com.team1.kingofhonor/" + R.raw.zhugeliang,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.zhugeliang_icon, true,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.zhugeliang1, "策谋之刻 冷却值：0消耗：0\n" +
                "\n" +
                "被动：诸葛亮法术命中敌人时，会为自己施加谋略刻印，当刻印达到5层时，会召唤5颗法球围绕自身，短暂时间后依次飞出攻击附近的敌人造成270(+52%法术加成)点法术伤害；法球在1秒内连续命中同一目标时，从第二颗法球开始将只造成20%伤害；法球会优先攻击敌方英雄，法球不会攻击非战斗状态的野怪",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.zhugeliang2, "东风破袭 冷却值：8/7.4/6.8/6.2/5.6/5消耗：60\n" +
                "\n" +
                "诸葛亮指定方向发射三颗法球，对路径上的敌人造成500/560/620/680/740/800（+75%法术加成）点法术伤害，如果多颗法球命中同一目标时，从第二颗法球开始将只造成30%伤害；每颗法球能触发1次谋略刻印",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.zhugeliang3,"时空穿梭 冷却值：2消耗：50\n" +
                "\n" +
                "诸葛亮快速朝指定方向闪烁，并在起始位置和终点位置同时产生一次法力场，对范围内敌人造成350/420/490/560/630/700（+52%法术加成）点法术伤害；同一目标如果在5秒内多次受到法力场影响，将会减少其90%移动速度，持续3秒，该效果会持续衰减；当目标被多个法力场同时命中，将只造成50%伤害；每次法力场影响到敌人，都会触发1次谋略刻印；每10秒可准备1次时空穿梭，最多可储备3次时空穿梭；",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.zhugeliang4,"元气弹 冷却值：35/30/25消耗：120\n" +
                "\n" +
                "诸葛亮短暂蓄力后锁定目标发出一击强力元气弹，造成450/600/750（+50%法术加成）点法术伤害；目标每损失1%最大生命值元气弹就会增加2%伤害；蓄力时诸葛亮可以移动，并且可以使用时空穿梭；元气弹飞行过程中碰撞到敌方非英雄目标，将会触发同等伤害并将其击飞0.5秒；元气弹可触发1次谋略刻印；如果元气弹直接击败敌方英雄，立即发动策谋之刻，并减少80%元气弹的冷却时间",
                "噬神之书","秘法之靴","痛苦面具","辉月","博学者之怒","虚无法杖"));
        heroSQLiteHelper.addHero(new Hero("后羿","android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.houyi,"半神之弓", "射手", 3, 8, 4, 3,
                "android.resource://com.team1.kingofhonor/" + R.raw.houyi,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.houyi_icon, false,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.houyi1, "惩戒射击 冷却值：0消耗：0\n" +
                "\n" +
                "后羿的普攻命中敌人后增加自身10%攻击速度，可叠加至多3层。当攻速加成叠加到3层时，后羿的普攻将射出3支箭矢，每支箭矢造成原伤害的40%，强化持续3秒。（期间每次命中刷新持续时间）",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.houyi2, "多重箭矢 冷却值：10消耗：60\n" +
                "\n" +
                "后羿强化自身攻击，每次攻击造成100/120/140/160/180/200（+80%物理加成）点物理伤害（若触发惩戒射击则每支箭矢造成原伤害的40%）并对面前区域内另外2名敌人造成50%伤害，该效果持续5秒",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.houyi3,"落日余晖 冷却值：8/7.4/6.8/6.2/5.6/5消耗：60\n" +
                "\n" +
                "后羿命令日炙塔对指定区域进行攻击，短暂时间后召唤一束激光打击指定位置。对命中的敌人造成240/280/320/360/400/440（+80%物理加成）点法术伤害和50%减速效果，持续2秒，被中心点命中的敌人将受到额外50%的伤害",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.houyi4,"灼日之矢 冷却值：45/40/35消耗：130\n" +
                "\n" +
                "后羿向前方射出火焰箭。击中敌方英雄时造成500/625/750（+140%物理加成）点物理伤害并晕眩此目标(晕眩时长取决于火焰箭的飞行距离，最多造成3.5秒晕眩)。目标周围的敌人会受到爆炸伤害",
                "末世","急速战靴","闪电匕首","无尽战刃","碎星锤","贤者的庇护"));
        heroSQLiteHelper.addHero(new Hero("关羽","android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.guanyu,"一骑当千", "战士", 6, 6, 6, 8,
                "android.resource://com.team1.kingofhonor/" + R.raw.guanyu,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.guanyu_icon, true,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.guanyu1, "一骑当千 冷却值：0消耗：0\n" +
                "\n" +
                "被动：关羽每主动移动100距离将增加2％移动速度；持续移动达到2000距离时会进入冲锋姿态；当关羽的移动速度被减少至375点以下或受到控制效果时将退出冲锋姿态；关羽面朝敌方移动时将增加20％移动速度；冲锋姿态：关羽的普通攻击会击退敌人并附带10％最大生命值的物理伤害",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.guanyu2, "单刀赴会 冷却值：8/7.2/6.4/5.6/4.8/4消耗：0\n" +
                "\n" +
                "关羽旋转大刀队附近的敌人造成215/430/645/860/1075/1290（+127％物理加成）点物理伤害；冲锋姿态：关羽向前发起冲锋造成（100％物理加成）（10％最大生命值）点物理伤害，并在结束时向前劈砍，造成250/500/750/1000/1250/1500（+150％物理加成）点物理伤害",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.guanyu3,"青龙偃月 冷却值：14消耗：0\n" +
                "\n" +
                "关羽清啸一声，解除自身控制效果并增加30％移动速度，持续2秒；冲锋姿态：关羽清啸一声，解除自身控制效果并向前跃击敌人将其击退，造成335/470/605/740/875/1010(+112％物理加成）点物理伤害",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.guanyu4,"刀锋铁骑 冷却值：70/60/50消耗：0\n" +
                "\n" +
                "关羽激发潜能，将减少50％的冲锋准备距离，持续10秒；冲锋姿态：关羽将召唤铁骑向前方突击撞退敌人，并造成（+100％物理加成）（+10％最大生命值）点物理伤害",
                "暗影战斧","抵抗之靴","碎星锤","霸者重装","破军","魔女斗篷"));
        heroSQLiteHelper.addHero(new Hero("马可波罗","android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.makeboluo,"远游之枪", "射手", 3, 6, 6, 5,
                "android.resource://com.team1.kingofhonor/" + R.raw.makeboluo,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.makeboluo_icon, false,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.makeboluo1, "连锁反应 冷却值：0消耗：0\n" +
                "\n" +
                "被动：马可波罗的普攻与技能伤害能够破坏目标的防御并回复50点能量，连续10次受到伤害后，目标每次受到马可波罗的伤害都会额外受到45（+15%物理加成）点真实伤害(该伤害可以暴击)，持续5秒",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.makeboluo2, "华丽左轮 冷却值：7/6.6/6.2/5.8/5.4/5消耗：70\n" +
                "\n" +
                "马可波罗向指定方向连续射击过程期间获得10%移速提升，每一枪造成150/165/180/195/210/225（+13%物理加成）点物理伤害都能触发普攻的法球效果，命中回复10点能量，额外攻击速度会影响技能射出的子弹数目，在攻速达到0/75%/150%时射出子弹数5/7/9",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.makeboluo3,"漫游之枪 冷却值：5消耗：70\n" +
                "\n" +
                "马可波罗向指定方向闪烁，立即出现在目标位置；被动：马可波罗身边500范围内存在敌方英雄时，提升12%/14%/16%/18%/20%/22%的伤害和15%/18%/21%/24%/27%/30%移动速度",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.makeboluo4,"狂热弹幕 冷却值：50/45/40消耗：70\n" +
                "\n" +
                "马可波罗向指定方向闪烁，立即出现在指定位置并向周围发射弹幕触发普攻的法球效果，造成120/180/240（+15%物理加成）点物理伤害，额外攻击速度会影响技能射出的弹幕波数，在攻速达到0/75%/150%时射出弹幕数10/13/16",
                "急速战靴","末世","纯净苍穹","破晓","破军","魔女斗篷"));
        heroSQLiteHelper.addHero(new Hero("赵云","android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.zhaoyun,"苍天翔龙", "战士", 6, 6, 6, 5,
                "android.resource://com.team1.kingofhonor/" + R.raw.zhaoyun,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.zhaoyun_icon, false,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.zhaoyun1, "龙鸣 冷却值：0消耗：0\n" +
                "\n" +
                "被动：赵云每损失3%最大生命值，就会获得减少1%所受到的伤害",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.zhaoyun2, "惊雷之龙 冷却值：7/6.4/5.8/5.2/4.6/4消耗：40\n" +
                "\n" +
                "赵云执枪向指定方向冲锋，对路径上的敌人造成190/214/238/262/286/310（+100%物理加成）点物理伤害；冲锋后的下一次普通攻击会造成65/74/83/92/101/110（+160%物理加成）点物理伤害并将其减少25%移动速度，持续2秒",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.zhaoyun3,"破云之龙 冷却值：8/7.2/6.4/5.6/4.8/4消耗：50\n" +
                "\n" +
                "赵云向指定方向连续3次刺出龙枪，对指定方向敌人每次造成140/158/176/194/212/230（+60%物理加成）点物理伤害；每次命中英雄后回复35/39/43/47/51/55（+14%物理加成）点生命值",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.zhaoyun4,"天翔之龙 冷却值：18/15/12消耗：100\n" +
                "\n" +
                "赵云跃向空中，向目标区域发动雷霆一击，对范围内敌人造成350/450/550（+130%物理加成）点物理伤害并击飞1秒；同时标记敌人为感电目标；赵云的普通攻击和技能伤害对感电目标会造成额外30/45/60（+13%物理加成）点法术伤害",
                "暗影战斧","抵抗之靴","宗师之力","魔女斗篷","破军","贤者的庇护"));
        heroSQLiteHelper.addHero(new Hero("蔡文姬","android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.caiwenji,"天籁弦音", "辅助", 5, 1, 8, 3,
                "android.resource://com.team1.kingofhonor/" + R.raw.caiwenji,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.caiwenji_icon, false,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.caiwenji1, "长歌行 冷却值：0消耗：0\n" +
                "\n" +
                "被动：当蔡文姬受到伤害时，自身会立刻增加70%持续衰减的移动速度，持续2秒，同时自身会每秒回复250（+50%法术加成）点生命值，持续2秒；长歌行每10秒只能触发一次",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.caiwenji2, "思无邪 冷却值：15/14.4/13.8/13.2/12.6/12消耗：100\n" +
                "\n" +
                "蔡文姬演奏乐曲，自身会增加20%移动速度，持续3秒，同时将为自身和周围的友方英雄每0.5秒恢复60/66/72/78/84/90（+20%法术加成）点生命值，持续3秒",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.caiwenji3,"胡笳乐 冷却值：9消耗：70\n" +
                "\n" +
                "蔡文姬向指定方向弹奏一束音波，命中后会在敌人间弹射，每次弹射造成250/290/330/370/410/450（+36%法术加成）点法术伤害并将其眩晕0.75秒；每束音波最多弹射6次，同一目标最多受到2次弹射效果，第二次弹射命中时将只造成50%的初始伤害",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.caiwenji4,"忘忧曲 冷却值：60消耗：120\n" +
                "\n" +
                "蔡文姬释放琴音围绕四周，每0.5秒为范围内血量最低的友方英雄回复100/150/200（+60%法术加成）点生命值，持续5秒，同时为其增加300/375/450（+25%法术加成）点物理和法术防御",
                "圣杯","抵抗之靴","不祥征兆","魔女斗篷","霸者重装","贤者的庇护"));
        heroSQLiteHelper.addHero(new Hero("刘邦","android.resource://" + getApplicationContext().getPackageName() + "/" +R.mipmap.liubang,"双面君主", "坦克", 8, 4, 6, 5,
                "android.resource://com.team1.kingofhonor/" + R.raw.liubang,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.liubang_icon, false,
                "android.resource://com.team1.kingofhonor/" + R.mipmap.liubang1, "君主野望 冷却值：0消耗：0\n" +
                "\n" +
                "被动：刘邦第三次普通攻击将附带最大生命值4-8%的法术伤害，增加幅度随英雄等级成长，该效果无法对机关造成伤害",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.liubang2, "霸业之盾 冷却值：10消耗：75\n" +
                "\n" +
                "刘邦开启护盾将自身笼罩，持续5秒，护盾可抵免500/650/800/950/1100/1250（+168）（最大生命值的5/6/7/8/9/10%）点伤害，结束时对周围造成400/520/640/760/880/1000（+134）（最大生命值的4/4/5/6/7/8%）点法术伤害；2秒后再次施放可立即结束护盾，若护盾被击破，则不会造成伤害",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.liubang3,"双重恐吓 冷却值：10/9.6/9.2/8.8/8.4/8消耗：70\n" +
                "\n" +
                "刘邦蓄力后挥剑冲锋300-500距离，对路径上敌人造成150/180/210/240/270/300~600（+60%物理加成）点法术伤害和0.5-1秒晕眩效果，冲锋距离、攻击伤害和晕眩时长和蓄力时间成正比。蓄力达2秒后各效果强度达到上限，最多可保存蓄力状态5秒，蓄力期间若取消或被打断，该技能会执行40%冷却时间",
                "android.resource://com.team1.kingofhonor/" + R.mipmap.liubang4,"统御战场 冷却值：70/65/60消耗：140\n" +
                "\n" +
                "刘邦指定一名队友并开始原地吟唱，持续2.2秒，期间刘邦和该队友将减少40%所受到的伤害，并且1技能损人利己将以援护队友作为添加护盾目标；吟唱后刘邦将传送到该队友位置；传送后刘邦增加30%移动速度，持续1.5秒；同时会生成一个跟随自身的法术场，法术场为范围内队友提供60/105/150点物理防御和法术防御，持续6秒",
                "暗影战斧","抵抗之靴","不祥征兆","魔女斗篷","霸者重装","贤者的庇护"));
        //添加各个页面
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add(R.string.title1, Fragment1.class)
                .add(R.string.title2, Fragment2.class)
                .add(R.string.title3, Fragment3.class)
                .add(R.string.title4, Fragment4.class)
                .create());
        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);

        heroSQLiteHelper.deleteAllEquipments();
        //添加装备数据
        equip = readEquipFromRaw();
        Log.e("get", equip[1]);
        for(int i = 0; i < equip.length/7; ++i){
            heroSQLiteHelper.addEquipment(new Equipment(equip[i*7], Integer.parseInt(equip[i*7+1]), equip[i*7+2], equip[i*7+3], equip[i*7+4], getResources().getIdentifier(equip[i*7+5], "mipmap", getPackageName()), equip[i*7+6]));
        }

        heroSQLiteHelper.deleteAllInscription();
        Inscription inscription = new Inscription("洞察","暴击", R.mipmap.dongcha, 2, "green");
        inscription.addProperty("暴击率", 0.1);
        inscription.addProperty("法术护甲穿透", 1.8);
        heroSQLiteHelper.addInscription(inscription);
        inscription = new Inscription("猛攻","攻速", R.mipmap.menggong, 1, "red");
        inscription.addProperty("攻击速度", 0.4);
        heroSQLiteHelper.addInscription(inscription);
        inscription = new Inscription("绽放","生命", R.mipmap.zhanfang, 3, "blue");
        inscription.addProperty("最大生命", 12);
        inscription.addProperty("每五秒回血", 4.2);
        heroSQLiteHelper.addInscription(inscription);

        ArrayList<Equipment> list = heroSQLiteHelper.getAllEquipment();
        Random random = new Random();
        int num = random.nextInt(list.size());
        Intent intentBroadcast = new Intent(MainActivity.this, MyReceiver1.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("equip", list.get(num));
        intentBroadcast.putExtras(bundle);
        sendBroadcast(intentBroadcast);
        Log.e("number",String.valueOf(list.size()));
    }

    //应用进入后台停止播放
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }
    //应用进入前台开始播放
    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
    }



// 对从文件中读取的装备信息进行分割存储
    private String[] readEquipFromRaw() {
        String[] res = new String[]{};
        try {
            InputStream is = getResources().openRawResource(R.raw.equipments);
            String text = readTextFromSDcard(is);
            res = text.split(",");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }
    // 从文件中读取装备信息
    private String readTextFromSDcard(InputStream is) throws Exception {
        InputStreamReader reader = new InputStreamReader(is, "GB2312");
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer buffer = new StringBuffer("");
        String str;
        while ((str = bufferedReader.readLine()) != null) {
            buffer.append(str);
            buffer.append(",");
        }
        return buffer.toString();
    }

}
