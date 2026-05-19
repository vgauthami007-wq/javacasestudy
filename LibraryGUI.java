import javax.swing.*; import javax.swing.border.*; import javax.swing.table.*;
import java.awt.*; import java.awt.event.*; import java.text.SimpleDateFormat; import java.util.Date;

public class LibraryGUI extends JFrame {
    // Palette
    private final Color PR=new Color(0,95,115),AC=new Color(10,147,150),BG=new Color(240,247,247);
    private final Color SB=new Color(27,42,43),CB=Color.WHITE,TD=new Color(27,42,43);
    private final Color DG=new Color(200,50,50),OK=new Color(30,140,80),WN=new Color(210,140,0);
    // State
    private Library lib=new Library(); private CardLayout cl=new CardLayout(); private JPanel cp;
    private JButton activeBtn; private final SimpleDateFormat SDF=new SimpleDateFormat("dd-MM-yyyy");
    private JLabel sBooks,sStudents,sBorrowed,sOverdue,sFine;
    private DefaultTableModel mActive,mBooks,mStudents,mHistory;

    public LibraryGUI() {
        seedData(); setTitle("Amrita Library System"); setSize(1150,700);
        setDefaultCloseOperation(EXIT_ON_CLOSE); setLocationRelativeTo(null); setLayout(new BorderLayout());
        cp=new JPanel(cl); cp.setBackground(BG);
        cp.add(dashboard(),"Dashboard"); cp.add(issuePanel(),"Issue"); cp.add(returnPanel(),"Return");
        cp.add(allBooksPanel(),"AllBooks"); cp.add(addBookPanel(),"AddBook");
        cp.add(allStudentsPanel(),"AllStudents"); cp.add(addStudentPanel(),"AddStudent");
        cp.add(historyPanel(),"History"); cp.add(fineCalcPanel(),"FineCalc");
        add(sidebar(),BorderLayout.WEST); add(cp,BorderLayout.CENTER);
        refreshStats(); refreshActive(); refreshBooks(); refreshStudents();
    }

    private void seedData() {
        lib.addBook(new Book(101,"OOP","Amrita Faculty")); lib.addBook(new Book(102,"Data Structures","Mark Weiss"));
        lib.addBook(new Book(103,"Design Patterns","Gang of Four")); lib.addBook(new Book(104,"Clean Code","R.C. Martin"));
        lib.addStudent(new Student(25008,"Abraham Harish")); lib.addStudent(new Student(25009,"Fida Fathima"));
        lib.addStudent(new Student(25010,"Rahul Menon"));
    }

    // Sidebar
    private JPanel sidebar() {
        JPanel s=panel(new BoxLayout(null,0)); s.setLayout(new BoxLayout(s,BoxLayout.Y_AXIS));
        s.setBackground(SB); s.setPreferredSize(new Dimension(215,0)); s.setBorder(new EmptyBorder(20,10,20,10));
        JLabel brand=lbl("  Library System",Color.WHITE,16,Font.BOLD); brand.setAlignmentX(0.5f); s.add(brand); s.add(Box.createVerticalStrut(6));
        JLabel clk=lbl("",new Color(140,170,170),11,Font.PLAIN); clk.setAlignmentX(0.5f); s.add(clk);
        new Timer(1000,e->clk.setText(new SimpleDateFormat("dd MMM yyyy  HH:mm:ss").format(new Date()))){{setInitialDelay(0);start();}};
        s.add(Box.createVerticalStrut(24));
        s.add(sec("GENERAL")); s.add(Box.createVerticalStrut(5));
        JButton d=nb("  Dashboard","Dashboard"); activeBtn=d; d.setBackground(PR);
        for(JButton b:new JButton[]{d,nb("  Issue Book","Issue"),nb("  Return Book","Return")}){s.add(b);s.add(Box.createVerticalStrut(5));}
        s.add(Box.createVerticalStrut(10)); s.add(sec("BOOKS")); s.add(Box.createVerticalStrut(5));
        for(JButton b:new JButton[]{nb("  All Books","AllBooks"),nb("  Add Book","AddBook")}){s.add(b);s.add(Box.createVerticalStrut(5));}
        s.add(Box.createVerticalStrut(10)); s.add(sec("STUDENTS")); s.add(Box.createVerticalStrut(5));
        for(JButton b:new JButton[]{nb("  All Students","AllStudents"),nb("  Add Student","AddStudent"),nb("  Borrow History","History")}){s.add(b);s.add(Box.createVerticalStrut(5));}
        s.add(Box.createVerticalStrut(10)); s.add(sec("TOOLS")); s.add(Box.createVerticalStrut(5)); s.add(nb("  Fine Calculator","FineCalc"));
        return s;
    }
    private JLabel sec(String t){JLabel l=lbl(t,new Color(110,145,145),10,Font.BOLD);l.setAlignmentX(0.5f);return l;}
    private JButton nb(String text,String card){
        JButton b=new JButton(text); b.setFont(new Font("Segoe UI",Font.PLAIN,13)); b.setForeground(new Color(220,230,230));
        b.setBackground(SB); b.setFocusPainted(false); b.setBorderPainted(false); b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setAlignmentX(0.5f); b.setMaximumSize(new Dimension(195,38)); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(new EmptyBorder(5,14,5,8));
        b.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){if(b!=activeBtn)b.setBackground(new Color(50,72,72));}
            public void mouseExited(MouseEvent e){if(b!=activeBtn)b.setBackground(SB);}
        });
        b.addActionListener(e->{
            if(activeBtn!=null)activeBtn.setBackground(SB); activeBtn=b; b.setBackground(PR);
            cl.show(cp,card);
            switch(card){case "Dashboard"->{refreshStats();refreshActive();}case "AllBooks"->refreshBooks();
                case "AllStudents"->refreshStudents();case "History"->fillHistory(-1);}
        });
        return b;
    }

    // Dashboard
    private JPanel dashboard(){
        JPanel p=padded(25); p.setLayout(new BorderLayout(15,15));
        JLabel h=lbl("Dashboard Overview",PR,22,Font.BOLD); p.add(h,BorderLayout.NORTH);
        sBooks=statLbl(PR); sStudents=statLbl(AC); sBorrowed=statLbl(new Color(30,120,180));
        sOverdue=statLbl(WN); sFine=statLbl(DG);
        JPanel row=new JPanel(new GridLayout(1,5,12,0)); row.setBackground(BG);
        row.add(card("Total Books",sBooks,PR)); row.add(card("Students",sStudents,AC));
        row.add(card("Borrowed",sBorrowed,new Color(30,120,180))); row.add(card("Overdue",sOverdue,WN));
        row.add(card("Total Fines Rs",sFine,DG));
        String[]cols={"Book ID","Title","Student","Borrow Date","Due Date","Status","Fine (Rs)"};
        mActive=tm(cols); JTable t=tbl(mActive);
        t.setDefaultRenderer(Object.class,colorRow(t,5,"Overdue",DG,OK));
        JPanel mid=new JPanel(new BorderLayout(0,12)); mid.setBackground(BG);
        mid.add(row,BorderLayout.NORTH); mid.add(scroll(t,lbl("Currently Borrowed Books",TD,14,Font.BOLD)),BorderLayout.CENTER);
        p.add(mid,BorderLayout.CENTER); return p;
    }
    private JLabel statLbl(Color c){JLabel l=new JLabel("0",SwingConstants.CENTER);l.setFont(new Font("Segoe UI",Font.BOLD,30));l.setForeground(c);return l;}
    private JPanel card(String title,JLabel num,Color accent){
        JPanel c=new JPanel(new BorderLayout(0,4)); c.setBackground(CB);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,4,0,0,accent),new EmptyBorder(14,12,14,12)));
        JLabel t=lbl(title,new Color(100,110,110),12,Font.PLAIN); t.setHorizontalAlignment(SwingConstants.CENTER);
        c.add(t,BorderLayout.NORTH); c.add(num,BorderLayout.CENTER); return c;
    }
    private void refreshStats(){
        int bor=(int)lib.bookList.stream().filter(b->!b.isAvailable()).count();
        int ov=(int)lib.activeRecords.stream().filter(BorrowRecord::isOverdue).count();
        double tf=lib.activeRecords.stream().mapToDouble(r->{if(!r.isOverdue())return 0;
            return(int)((new Date().getTime()-r.dueDate.getTime())/(86400000L))*10.0;}).sum();
        sBooks.setText(""+lib.bookList.size()); sStudents.setText(""+lib.studentList.size());
        sBorrowed.setText(""+bor); sOverdue.setText(""+ov); sFine.setText(String.format("%.0f",tf));
    }
    private void refreshActive(){
        if(mActive==null)return; mActive.setRowCount(0);
        for(BorrowRecord r:lib.activeRecords){
            boolean ov=r.isOverdue(); double fine=ov?(int)((new Date().getTime()-r.dueDate.getTime())/86400000L)*10.0:0;
            String sn=lib.studentList.stream().filter(s->s.studentId==r.studentId).map(s->s.name).findFirst().orElse("?");
            mActive.addRow(new Object[]{r.book.bookId,r.book.title,r.studentId+"-"+sn,
                SDF.format(r.borrowDate),SDF.format(r.dueDate),ov?"Overdue":"On Time",String.format("%.2f",fine)});
        }
    }

    // Issue
    private JPanel issuePanel(){
        JTextField sid=field(),bid=field(); JLabel msg=msgLbl(),due=lbl(" ",AC,13,Font.ITALIC);
        due.setAlignmentX(0.5f);
        JButton btn=btn("Issue Book",PR); btn.addActionListener(e->{
            try{String r=lib.issueBook(parseInt(sid),parseInt(bid)); msg(msg,r);
                if(r.startsWith("Success")){due.setText("Due: "+SDF.format(new Date(System.currentTimeMillis()+604800000L)));
                    sid.setText("");bid.setText("");refreshStats();refreshActive();refreshBooks();}else due.setText(" ");
            }catch(Exception ex){msg(msg,"Error: Enter valid numeric IDs.");}
        });
        return form("Issue Book",new Object[]{"Student ID:",sid,"Book ID:",bid},btn,due,msg);
    }

    // Return
    private JPanel returnPanel(){
        JTextField bid=field(); JLabel prev=lbl("Enter Book ID to preview fine",new Color(100,120,120),13,Font.ITALIC),msg=msgLbl();
        prev.setHorizontalAlignment(SwingConstants.CENTER);
        bid.addKeyListener(new KeyAdapter(){public void keyReleased(KeyEvent e){
            try{int id=Integer.parseInt(bid.getText().trim());
                BorrowRecord r=lib.activeRecords.stream().filter(x->x.book.bookId==id).findFirst().orElse(null);
                if(r==null){prev.setText("No active borrow");prev.setForeground(WN);return;}
                if(!r.isOverdue()){prev.setText("On time - no fine");prev.setForeground(OK);return;}
                int d=(int)((new Date().getTime()-r.dueDate.getTime())/86400000L);
                prev.setText(d+" day(s) late - Fine: Rs."+String.format("%.2f",d*10.0));prev.setForeground(DG);
            }catch(Exception ex){prev.setText("Enter Book ID to preview fine");prev.setForeground(new Color(100,120,120));}
        }});
        JButton btn=btn("Confirm Return",AC); btn.addActionListener(e->{
            try{String r=lib.returnBook(parseInt(bid)); msg(msg,r);
                if(!r.startsWith("Error")){bid.setText("");prev.setText("Enter Book ID to preview fine");
                    prev.setForeground(new Color(100,120,120));refreshStats();refreshActive();refreshBooks();}
            }catch(Exception ex){msg(msg,"Error: Enter a valid Book ID.");}
        });
        JPanel pb=new JPanel(new BorderLayout()); pb.setBackground(new Color(245,250,250));
        pb.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180,210,210)),new EmptyBorder(10,10,10,10)));
        pb.add(lbl("Fine Preview",TD,12,Font.BOLD),BorderLayout.NORTH); pb.add(prev,BorderLayout.CENTER);
        JPanel c=formCard("Return Book"); c.add(row("Book ID:",bid)); c.add(Box.createVerticalStrut(12));
        c.add(pb); c.add(Box.createVerticalStrut(14)); c.add(btn); c.add(Box.createVerticalStrut(8)); c.add(msg);
        return centered(c);
    }

    // All the Books
    private JPanel allBooksPanel(){
        String[]cols={"Book ID","Title","Author","Status"};
        mBooks=tm(cols); JTable t=tbl(mBooks);
        t.getColumnModel().getColumn(3).setCellRenderer(colorCell(OK,DG));
        JTextField sf=field(); sf.setPreferredSize(new Dimension(200,28));
        sf.addKeyListener(new KeyAdapter(){public void keyReleased(KeyEvent e){
            String q=sf.getText().trim().toLowerCase(); mBooks.setRowCount(0);
            lib.bookList.stream().filter(b->q.isEmpty()||(""+b.bookId).contains(q)||b.title.toLowerCase().contains(q)||b.author.toLowerCase().contains(q))
                .forEach(b->mBooks.addRow(new Object[]{b.bookId,b.title,b.author,b.isAvailable()?"Available":"Borrowed"}));
        }});
        JButton ref=btn("Refresh",PR); ref.setMaximumSize(new Dimension(100,30));
        ref.addActionListener(e->{sf.setText("");refreshBooks();});
        return tablePanel("All Books",sf,t,ref);
    }
    private void refreshBooks(){if(mBooks==null)return;mBooks.setRowCount(0);
        lib.bookList.forEach(b->mBooks.addRow(new Object[]{b.bookId,b.title,b.author,b.isAvailable()?"Available":"Borrowed"}));}

    // Add Book
    private JPanel addBookPanel(){
        JTextField id=field(),ti=field(),au=field(); JLabel msg=msgLbl();
        JButton btn=btn("Add Book",OK); btn.addActionListener(e->{
            try{int bid=Integer.parseInt(id.getText().trim()); String t=ti.getText().trim(),a=au.getText().trim();
                if(t.isEmpty()||a.isEmpty()){msg(msg,"Error: Title/Author required.");return;}
                if(lib.bookList.stream().anyMatch(b->b.bookId==bid)){msg(msg,"Error: Book ID exists.");return;}
                lib.addBook(new Book(bid,t,a)); msg(msg,"Success: Book added!"); id.setText("");ti.setText("");au.setText("");
                refreshStats();refreshBooks();
            }catch(Exception ex){msg(msg,"Error: ID must be a number.");}
        });
        return form("Add New Book",new Object[]{"Book ID:",id,"Title:",ti,"Author:",au},btn,msg);
    }

    // All Students
    private JPanel allStudentsPanel(){
        String[]cols={"Student ID","Name","Total Borrows","Active","Fines Paid (Rs)"};
        mStudents=tm(cols); JTable t=tbl(mStudents);
        JTextField sf=field(); sf.setPreferredSize(new Dimension(200,28));
        sf.addKeyListener(new KeyAdapter(){public void keyReleased(KeyEvent e){fillStudents(sf.getText().trim().toLowerCase());}});
        JButton ref=btn("Refresh",PR); ref.setMaximumSize(new Dimension(100,30));
        ref.addActionListener(e->{sf.setText("");refreshStudents();});
        return tablePanel("All Students",sf,t,ref);
    }
    private void refreshStudents(){fillStudents("");}
    private void fillStudents(String q){
        if(mStudents==null)return; mStudents.setRowCount(0);
        lib.studentList.stream().filter(s->q.isEmpty()||(""+s.studentId).contains(q)||s.name.toLowerCase().contains(q))
            .forEach(s->{long ac=lib.activeRecords.stream().filter(r->r.studentId==s.studentId).count();
                double fp=s.records.stream().filter(r->r.returnDate!=null&&r.calculateFine()>0).mapToDouble(BorrowRecord::calculateFine).sum();
                mStudents.addRow(new Object[]{s.studentId,s.name,s.records.size(),ac,String.format("%.2f",fp)});});
    }

    // Add Student
    private JPanel addStudentPanel(){
        JTextField id=field(),nm=field(); JLabel msg=msgLbl();
        JButton btn=btn("Add Student",OK); btn.addActionListener(e->{
            try{int sid=Integer.parseInt(id.getText().trim()); String n=nm.getText().trim();
                if(n.isEmpty()){msg(msg,"Error: Name required.");return;}
                if(lib.studentList.stream().anyMatch(s->s.studentId==sid)){msg(msg,"Error: Student ID exists.");return;}
                lib.addStudent(new Student(sid,n)); msg(msg,"Success: Student added!"); id.setText("");nm.setText("");
                refreshStats();refreshStudents();
            }catch(Exception ex){msg(msg,"Error: ID must be a number.");}
        });
        return form("Add New Student",new Object[]{"Student ID:",id,"Name:",nm},btn,msg);
    }

    // ── HISTORY
    private JPanel historyPanel(){
        String[]cols={"Book ID","Title","Std ID","Student","Borrow","Due","Returned","Days Late","Fine (Rs)","Status"};
        mHistory=tm(cols); JTable t=tbl(mHistory);
        t.setDefaultRenderer(Object.class,new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable tb,Object v,boolean s,boolean f,int r,int c){
                Component co=super.getTableCellRendererComponent(tb,v,s,f,r,c);
                if(!s)switch((String)tb.getModel().getValueAt(r,9)){
                    case "Late"->co.setForeground(DG);case "Returned"->co.setForeground(OK);
                    case "Overdue"->{co.setForeground(WN);co.setFont(co.getFont().deriveFont(Font.BOLD));}
                    default->co.setForeground(TD);}
                return co;}});
        JTextField sf=field(); sf.setPreferredSize(new Dimension(130,28)); JLabel sum=lbl(" ",TD,13,Font.BOLD);
        JButton all=btn("Show All",PR),find=btn("Filter",AC);
        all.setMaximumSize(new Dimension(110,30)); find.setMaximumSize(new Dimension(110,30));
        all.addActionListener(e->{fillHistory(-1);sum.setText("All "+mHistory.getRowCount()+" record(s)");});
        find.addActionListener(e->{try{int id=Integer.parseInt(sf.getText().trim());fillHistory(id);
            sum.setText(mHistory.getRowCount()+" record(s) for Std "+id);}catch(Exception ex){fillHistory(-1);}});
        fillHistory(-1);
        JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT,6,0)); top.setBackground(BG);
        top.add(lbl("Borrow History",PR,20,Font.BOLD)); top.add(Box.createHorizontalStrut(16));
        top.add(lbl("Student ID:",TD,13,Font.PLAIN)); top.add(sf); top.add(find); top.add(all);
        JPanel bot=new JPanel(new FlowLayout(FlowLayout.LEFT)); bot.setBackground(BG); bot.add(sum);
        JPanel p=padded(20); p.setLayout(new BorderLayout(0,10));
        p.add(top,BorderLayout.NORTH); p.add(new JScrollPane(t),BorderLayout.CENTER); p.add(bot,BorderLayout.SOUTH);
        return p;
    }
    private void fillHistory(int fid){
        if(mHistory==null)return; mHistory.setRowCount(0);
        for(Student s:lib.studentList){if(fid!=-1&&s.studentId!=fid)continue;
            for(BorrowRecord r:s.records){int dl=r.calculateDaysLate();double fi=r.calculateFine();
                String st=r.returnDate==null?(r.isOverdue()?"Overdue":"Active"):(dl>0?"Late":"Returned");
                mHistory.addRow(new Object[]{r.book.bookId,r.book.title,s.studentId,s.name,
                    SDF.format(r.borrowDate),SDF.format(r.dueDate),r.returnDate!=null?SDF.format(r.returnDate):"-",
                    dl>0?dl:0,String.format("%.2f",fi),st});}}
    }

    // ── FINE CALCULATOR
    private JPanel fineCalcPanel(){
        JPanel c=formCard("Fine Calculator");
        // Section A - manual
        c.add(lbl("Manual Calculation",PR,14,Font.BOLD));c.add(Box.createVerticalStrut(8));
        JTextField days=field(),rate=field(); rate.setText("10.00");
        JLabel resA=lbl("Fine: Rs. 0.00",DG,22,Font.BOLD); resA.setAlignmentX(0.5f); resA.setHorizontalAlignment(SwingConstants.CENTER);
        JButton calcA=btn("Calculate",PR); calcA.addActionListener(e->{
            try{int d=Integer.parseInt(days.getText().trim());double ra=Double.parseDouble(rate.getText().trim());
                double f=d*ra; resA.setText("Fine: Rs."+String.format("%.2f",f));resA.setForeground(f>0?DG:OK);
            }catch(Exception ex){resA.setText("Invalid input");}});
        c.add(row("Days Late:",days));c.add(Box.createVerticalStrut(8));
        c.add(row("Rate/Day (Rs):",rate));c.add(Box.createVerticalStrut(10));c.add(calcA);c.add(Box.createVerticalStrut(8));c.add(resA);
        // Separator
        c.add(Box.createVerticalStrut(14));JSeparator sep=new JSeparator();sep.setMaximumSize(new Dimension(9999,1));c.add(sep);c.add(Box.createVerticalStrut(14));
        // Section B - lookup
        c.add(lbl("Lookup by Active Book ID",PR,14,Font.BOLD));c.add(Box.createVerticalStrut(8));
        JTextField bid=field(); JLabel resB=lbl(" ",TD,14,Font.BOLD); resB.setAlignmentX(0.5f); resB.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel vBook=lbl("-",TD,12,Font.PLAIN),vStu=lbl("-",TD,12,Font.PLAIN),vBor=lbl("-",TD,12,Font.PLAIN);
        JLabel vDue=lbl("-",TD,12,Font.PLAIN),vDays=lbl("-",TD,12,Font.PLAIN),vFine=lbl("-",DG,12,Font.BOLD);
        JPanel grid=new JPanel(new GridLayout(0,2,6,6)); grid.setBackground(new Color(245,250,250));
        grid.setBorder(new EmptyBorder(10,12,10,12));
        for(Object[]r:new Object[][]{{"Book:",vBook},{"Student:",vStu},{"Borrow:",vBor},{"Due:",vDue},{"Days Late:",vDays},{"Fine Rs:",vFine}})
            {grid.add(lbl((String)r[0],new Color(80,100,100),12,Font.BOLD));grid.add((JLabel)r[1]);}
        JButton calcB=btn("Lookup Fine",AC); calcB.addActionListener(e->{
            try{int id=Integer.parseInt(bid.getText().trim());
                BorrowRecord r=lib.activeRecords.stream().filter(x->x.book.bookId==id).findFirst().orElse(null);
                if(r==null){resB.setText("No active borrow for ID "+id);resB.setForeground(WN);return;}
                String sn=lib.studentList.stream().filter(s->s.studentId==r.studentId).map(s->s.name).findFirst().orElse("?");
                boolean ov=r.isOverdue(); int d=ov?(int)((new Date().getTime()-r.dueDate.getTime())/86400000L):0; double f=d*10.0;
                vBook.setText(r.book.title);vStu.setText(r.studentId+"-"+sn);vBor.setText(SDF.format(r.borrowDate));
                vDue.setText(SDF.format(r.dueDate));vDays.setText(ov?d+" day(s)":"0 (on time)");
                vFine.setText(String.format("Rs.%.2f",f));vFine.setForeground(f>0?DG:OK);
                resB.setText(ov?"Book is OVERDUE!":"Book is on time");resB.setForeground(ov?DG:OK);
            }catch(Exception ex){resB.setText("Enter a valid Book ID");resB.setForeground(WN);}});
        c.add(row("Book ID:",bid));c.add(Box.createVerticalStrut(10));c.add(calcB);c.add(Box.createVerticalStrut(8));c.add(resB);c.add(Box.createVerticalStrut(8));c.add(grid);
        return centered(c);
    }

    // ── SHARED HELPERS
    private JTable tbl(DefaultTableModel m){
        JTable t=new JTable(m){public Component prepareRenderer(TableCellRenderer r,int row,int col){
            Component c=super.prepareRenderer(r,row,col);
            if(!isRowSelected(row))c.setBackground(row%2==0?CB:new Color(232,242,242));return c;}};
        t.setFont(new Font("Segoe UI",Font.PLAIN,13));t.setRowHeight(26);t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0,0));t.setSelectionBackground(AC);t.setSelectionForeground(CB);t.setFillsViewportHeight(true);
        JTableHeader h=t.getTableHeader();h.setFont(new Font("Segoe UI",Font.BOLD,13));
        h.setBackground(PR);h.setForeground(CB);h.setReorderingAllowed(false);
        ((DefaultTableCellRenderer)h.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
        return t;
    }
    private DefaultTableModel tm(String[]cols){return new DefaultTableModel(cols,0){public boolean isCellEditable(int r,int c){return false;}};}
    private JPanel formCard(String title){
        JPanel c=new JPanel();c.setLayout(new BoxLayout(c,BoxLayout.Y_AXIS));c.setBackground(CB);
        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(190,215,215)),new EmptyBorder(24,30,24,30)));
        JLabel l=lbl(title,PR,18,Font.BOLD);l.setAlignmentX(0.5f);c.add(l);c.add(Box.createVerticalStrut(18));return c;
    }
    private JPanel row(String label,JTextField f){
        JPanel r=new JPanel(new BorderLayout(8,0));r.setBackground(CB);r.setMaximumSize(new Dimension(9999,34));
        JLabel l=lbl(label,TD,13,Font.PLAIN);l.setPreferredSize(new Dimension(145,28));r.add(l,BorderLayout.WEST);r.add(f,BorderLayout.CENTER);return r;
    }
    private JPanel form(String title,Object[]rows,JComponent...extras){
        JPanel c=formCard(title);
        for(int i=0;i<rows.length;i+=2){c.add(row((String)rows[i],(JTextField)rows[i+1]));c.add(Box.createVerticalStrut(8));}
        c.add(Box.createVerticalStrut(8));
        for(JComponent x:extras){c.add(x);c.add(Box.createVerticalStrut(6));}
        return centered(c);
    }
    private JPanel tablePanel(String title,JTextField search,JTable t,JButton ref){
        JPanel p=padded(20);p.setLayout(new BorderLayout(0,10));
        JPanel top=new JPanel(new FlowLayout(FlowLayout.LEFT,6,0));top.setBackground(BG);
        top.add(lbl(title,PR,20,Font.BOLD));top.add(Box.createHorizontalStrut(16));
        top.add(lbl("Search:",TD,13,Font.PLAIN));top.add(search);
        JPanel bot=new JPanel(new FlowLayout(FlowLayout.RIGHT));bot.setBackground(BG);bot.add(ref);
        p.add(top,BorderLayout.NORTH);p.add(new JScrollPane(t),BorderLayout.CENTER);p.add(bot,BorderLayout.SOUTH);return p;
    }
    private JPanel scroll(JTable t,JLabel header){
        JPanel p=new JPanel(new BorderLayout(0,8));p.setBackground(BG);p.add(header,BorderLayout.NORTH);p.add(new JScrollPane(t),BorderLayout.CENTER);return p;
    }
    private JPanel centered(JPanel c){JPanel o=new JPanel(new GridBagLayout());o.setBackground(BG);o.add(c);return o;}
    private JPanel padded(int p){JPanel pan=new JPanel();pan.setBackground(BG);pan.setBorder(new EmptyBorder(p,p,p,p));return pan;}
    private JPanel panel(LayoutManager l){JPanel p=new JPanel(l);p.setBackground(BG);return p;}
    private JLabel lbl(String t,Color c,int sz,int style){JLabel l=new JLabel(t);l.setFont(new Font("Segoe UI",style,sz));l.setForeground(c);return l;}
    private JTextField field(){JTextField f=new JTextField();f.setFont(new Font("Segoe UI",Font.PLAIN,13));
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(180,210,210)),new EmptyBorder(4,8,4,8)));return f;}
    private JButton btn(String t,Color bg){JButton b=new JButton(t);b.setFont(new Font("Segoe UI",Font.BOLD,13));
        b.setBackground(bg);b.setForeground(CB);b.setFocusPainted(false);b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));b.setAlignmentX(0.5f);b.setMaximumSize(new Dimension(9999,36));return b;}
    private JLabel msgLbl(){JLabel l=lbl(" ",TD,13,Font.BOLD);l.setHorizontalAlignment(SwingConstants.CENTER);l.setAlignmentX(0.5f);return l;}
    private void msg(JLabel l,String t){l.setText(t);l.setForeground(t.startsWith("Success")?OK:DG);}
    private int parseInt(JTextField f){return Integer.parseInt(f.getText().trim());}
    private TableCellRenderer colorRow(JTable t,int col,String match,Color yes,Color no){
        return new DefaultTableCellRenderer(){public Component getTableCellRendererComponent(JTable tb,Object v,boolean s,boolean f,int r,int c){
            Component co=super.getTableCellRendererComponent(tb,v,s,f,r,c);
            if(!s){String st=(String)tb.getModel().getValueAt(r,col);co.setForeground(match.equals(st)?yes:no);
                co.setFont(co.getFont().deriveFont(match.equals(st)?Font.BOLD:Font.PLAIN));}return co;}};
    }
    private TableCellRenderer colorCell(Color avail,Color borrow){
        return new DefaultTableCellRenderer(){public Component getTableCellRendererComponent(JTable tb,Object v,boolean s,boolean f,int r,int c){
            Component co=super.getTableCellRendererComponent(tb,v,s,f,r,c);
            if(!s){co.setForeground("Available".equals(v)?avail:borrow);co.setFont(co.getFont().deriveFont(Font.BOLD));}
            ((JLabel)co).setHorizontalAlignment(SwingConstants.CENTER);return co;}};
    }

    public static void main(String[]args){SwingUtilities.invokeLater(()->new LibraryGUI().setVisible(true));}
}