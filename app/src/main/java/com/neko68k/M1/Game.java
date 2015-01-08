package com.neko68k.M1;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Game implements Parcelable {
	int index;
	String title;
	String year;
	long intyear;
	String romname;
	String mfg;
	long intmfg;
	String sys;
	long intsys;
	String soundhw;
	String cpu;
	long intcpu;
	String sound1;
	long intsound1;
	String sound2;
	long intsound2;
	String sound3;
	long intsound3;
	String sound4;
	long intsound4;
	Integer romavail;
	Integer fave;

	public Game() {
		this.index = 0;
		this.title = "";
		this.year = "";
		this.romname = "";
		this.mfg = "";
		this.sys = "";
		this.cpu = "";
		this.sound1 = "";
		this.sound2 = "";
		this.sound3 = "";
		this.sound4 = "";
		this.soundhw = "";
		this.intmfg=0;
		this.intyear=0;
		this.intsys=0;
		this.intcpu=0;
		this.intsound1=0;
		this.intsound2=0;
		this.intsound3=0;
		this.intsound4=0;
		this.fave=0;
		// this.sound5="";
		// this.listavail=0;
	}

	public Game(Cursor cursor) {
		int tblYear = cursor.getColumnIndexOrThrow(GameListOpenHelper.KEY_YEAR);
		int tblMfg = cursor.getColumnIndexOrThrow(GameListOpenHelper.KEY_MFG);
		int tblBoard = cursor.getColumnIndexOrThrow(GameListOpenHelper.KEY_SYS);
		int tblTitle = cursor.getColumnIndexOrThrow(GameListOpenHelper.KEY_TITLE);
		/*int tblHardware = cursor
				.getColumnIndexOrThrow(GameListOpenHelper.KEY_CPU);
		int tblSound1 = cursor
				.getColumnIndexOrThrow(GameListOpenHelper.KEY_SOUND1);
		int tblSound2 = cursor
				.getColumnIndexOrThrow(GameListOpenHelper.KEY_SOUND2);
		int tblSound3 = cursor
				.getColumnIndexOrThrow(GameListOpenHelper.KEY_SOUND3);
		int tblSound4 = cursor
				.getColumnIndexOrThrow(GameListOpenHelper.KEY_SOUND4);*/
		int tblRomname = cursor
				.getColumnIndexOrThrow(GameListOpenHelper.KEY_ROMNAME);
		int tblId = cursor.getColumnIndexOrThrow(GameListOpenHelper.KEY_ID);
		int tblSoundhw = cursor.getColumnIndexOrThrow(GameListOpenHelper.KEY_SOUNDHW);
		index = cursor.getInt(tblId);
		year = cursor.getString(tblYear);
		mfg = cursor.getString(tblMfg);
		sys = cursor.getString(tblBoard);
		title = cursor.getString(tblTitle);
		//cpu = cursor.getString(tblHardware);
		//sound1 = cursor.getString(tblSound1);
		romname = cursor.getString(tblRomname);
		soundhw = cursor.getString(tblSoundhw);
		this.fave=0;
		this.intmfg=0;
		this.intyear=0;
		this.intsys=0;
		this.intcpu=0;
		this.intsound1=0;
		this.intsound2=0;
		this.intsound3=0;
		this.intsound4=0;
		return;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getRomname() {
		return romname;
	}

	public void setRomname(String romname) {
		this.romname = romname;
	}

	public String getMfg() {
		return mfg;
	}

	public void setMfg(String mfg) {
		this.mfg = mfg;
	}

	public String getSys() {
		return sys;
	}

	public void setSys(String sys) {
		this.sys = sys;
	}

	public String getCpu() {
		return cpu;
	}

	public void setCpu(String cpu) {
		this.cpu = cpu;
	}

	public String getSound1() {
		return sound1;
	}

	public void setSound1(String sound1) {
		this.sound1 = sound1;
	}

	public String getSound2() {
		return sound2;
	}

	public void setSound2(String sound2) {
		this.sound2 = sound2;
	}

	public String getSound3() {
		return sound3;
	}

	public void setSound3(String sound3) {
		this.sound3 = sound3;
	}

	public String getSound4() {
		return sound4;
	}

	public void setSound4(String sound4) {
		this.sound4 = sound4;
	}

	public long getIntyear() {
		return intyear;
	}

	public void setIntyear(long intyear) {
		this.intyear = intyear;
	}

	public long getIntmfg() {
		return intmfg;
	}

	public void setIntmfg(long intmfg) {
		this.intmfg = intmfg;
	}

	public long getIntsys() {
		return intsys;
	}

	public void setIntsys(long intsys) {
		this.intsys = intsys;
	}

	public String getSoundhw() {
		return soundhw;
	}

	public void setSoundhw(String soundhw) {
		this.soundhw = soundhw;
	}

	public long getIntcpu() {
		return intcpu;
	}

	public void setIntcpu(long intcpu) {
		this.intcpu = intcpu;
	}

	public long getIntsound1() {
		return intsound1;
	}

	public void setIntsound1(long intsound1) {
		this.intsound1 = intsound1;
	}

	public long getIntsound2() {
		return intsound2;
	}

	public void setIntsound2(long intsound2) {
		this.intsound2 = intsound2;
	}

	public long getIntsound3() {
		return intsound3;
	}

	public void setIntsound3(long intsound3) {
		this.intsound3 = intsound3;
	}

	public long getIntsound4() {
		return intsound4;
	}

	public void setIntsound4(long intsound4) {
		this.intsound4 = intsound4;
	}

	public long getromavail() {
		return romavail;
	}

	public void setromavail(Integer romavail) {
		this.romavail = romavail;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel out, int flags) {
		// TODO Auto-generated method stub
		
		

		out.writeInt(index);
		out.writeString(title);
		out.writeString(year);
		out.writeString(romname);
		out.writeString(mfg);
		out.writeString(sys);
		out.writeString(cpu);
		out.writeString(sound1);
		out.writeString(sound2);
		out.writeString(sound3);
		out.writeString(sound4);
		out.writeLong(intyear);
		out.writeLong(intmfg);
		out.writeLong(intsys);
		out.writeLong(intcpu);
		out.writeLong(intsound1);
		out.writeLong(intsound2);
		out.writeLong(intsound3);
		out.writeLong(intsound4);
		out.writeString(soundhw);
		out.writeInt(fave);
		// out.writeInt(romavail);

	}

	public Game(Parcel in) {
		index = in.readInt();
		title = in.readString();
		year = in.readString();
		romname = in.readString();
		mfg = in.readString();
		sys = in.readString();
		cpu = in.readString();
		sound1 = in.readString();
		sound2 = in.readString();
		sound3 = in.readString();
		sound4 = in.readString();
		intyear = in.readInt();
		intmfg = in.readInt();
		intsys = in.readInt();
		intcpu = in.readInt();
		intsound1 = in.readInt();
		intsound2 = in.readInt();
		intsound3 = in.readInt();
		intsound4 = in.readInt();
		soundhw = in.readString();
		fave = in.readInt();
		// romavail = in.readInt();
	}

	public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {
		// @Override
		public Game createFromParcel(Parcel source) {
			return new Game(source);
		}

		// @Override
		public Game[] newArray(int size) {
			return new Game[size];
		}
	};
}
