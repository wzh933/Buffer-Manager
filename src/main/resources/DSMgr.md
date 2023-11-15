#### Data Storage Interface Functions

当前数据文件将保存在DSMgr类中。该文件将被命名为data.dbf。

***OpenFile(string filename)***

OpenFile函数在任何需要打开文件进行阅读或写的时候被调用。这个函数的原型是OpenFile（String文件名），并返回一个错误代码。函数打开文件名指定的文件。

***CloseFile()***

当需要关闭数据文件时，调用CloseFile函数。原型是CloseFile（）并返回错误代码。此函数关闭当前使用的文件。只有在数据库被更改或程序关闭时才应调用此函数。

***ReadPage(int page_id)***

ReadPage函数由缓冲区管理器中的FixPage函数调用。这个原型是ReadPage（page_id，bytes）并返回它所读入的内容。此函数调用fseek（）和fread（）从文件中获取数据。

***WritePage(int frame_id, bFrame frm)***

只要从缓冲区中取出一个页，就会调用WritePage函数。原型是WritePage（frame_id，frm），返回写入的字节数。此函数调用fseek（）和fwrite（）将数据保存到文件中。

***Seek(int offset, int pos)***

Seek函数移动文件指针。

***GetFile()***

GetFile函数返回当前文件。

***IncNumPages()***

IncNumPages函数递增页面计数器。

***GetNumPages()***

GetNumPages函数返回页面计数器。

***SetUse(int page_id, int use_bit)***

SetUse函数看起来设置页面数组中的位。此数组跟踪正在使用的页面。如果一个页面中的所有记录都被删除了，那么这个页面实际上就不再被使用了，并且可以在数据库中再次被重用。为了知道页面是否可重用，检查数组中是否有任何use_bits被设置为零。fixNewPage函数首先检查此数组的use_bit是否为零。如果找到一个，则重用该页。如果没有，则分配新页面。

***GetUse(int page_id)***

GetUse函数返回对应page_id的当前use_bit。
