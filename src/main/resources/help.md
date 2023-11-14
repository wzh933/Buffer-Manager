class DSMgr
{
public:
DSMgr();
int OpenFile(string filename);
int CloseFile();
bFrame ReadPage(int page_id);
int WritePage(int frame_id, bFrame frm);
int Seek(int offset, int pos);
FILE * GetFile();
void IncNumPages();
int GetNumPages();
void SetUse(int index, int use_bit);
int GetUse(int index);
private:
FILE *currFile;
int numPages;
int pages[MAXPAGES];
};

class BMgr
{
public:
BMgr();
// Interface functions
int FixPage(int page_id, int prot);
void NewPage FixNewPage();
int UnfixPage(int page_id);
int NumFreeFrames();
// Internal Functions
int SelectVictim();
int Hash(int page_id);
void RemoveBCB(BCB * ptr, int page_id);
void RemoveLRUEle(int frid);
void SetDirty(int frame_id);
void UnsetDirty(int （frame_id);
void WriteDirtys();
PrintFrame(int frame_id);
private:
// Hash Table
int ftop[DEFBUFSIZE];
BCB* ptof[DEFBUFSIZE];
};
