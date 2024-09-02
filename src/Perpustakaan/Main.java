package Perpustakaan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Supplier;

abstract class Item {  // abstract 
    protected String title; // modifier + attibut

    public Item(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public abstract void displayInfo(); //abstract
}

class Book extends Item { //class
    private final String keterangan;

    public Book(String title, String keterangan) {
        super(title);
        this.keterangan = keterangan; //attribut
    }

    public String getAuthor() {
        return keterangan;
    }

    @Override  //ovveride
    public void displayInfo() { // methode
        System.out.println("Judul buku : " + title);
        System.out.println("pinjaman buku : " + keterangan);
    }
}

class Peminjam { //class
    private final String nama; //modifier
    private final String nbi;  //modifier

    public Peminjam(String nama, String nbi) {
        this.nama = nama;
        this.nbi = nbi;
    }

    public String getNama() {
        return nama;
    }

    public String getNbi() {
        return nbi;
    }
}

class Pinjaman {
    private final Peminjam peminjam;
    private final Item item;
    private final Date tanggalPeminjaman;
    private final Date tanggalPengembalian;

    public Pinjaman(Peminjam peminjam, Item item, Date tanggalPeminjaman) {
        this.peminjam = peminjam;
        this.item = item;
        this.tanggalPeminjaman = tanggalPeminjaman;
        this.tanggalPengembalian = hitungTanggalPengembalian(tanggalPeminjaman);
    }

    public Peminjam getPeminjam() {
        return peminjam;
    }

    public Item getItem() {
        return item;
    }

    public Date getTanggalPeminjaman() {
        return tanggalPeminjaman;
    }

    public Date getTanggalPengembalian() {
        return tanggalPengembalian;
    }

    private Date hitungTanggalPengembalian(Date tanggalPeminjaman) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(tanggalPeminjaman);
        cal.add(Calendar.DATE, 7); // Menambahkan 7 hari

        Date currentDate = new Date();
        if (cal.getTime().before(currentDate)) {
            return hitungTanggalPengembalian(cal.getTime()); // Rekursif 
        } else {
            return cal.getTime();
        }
    }
}
//read and write
class FileManager {
    public static void writeToFile(String fileName, Supplier<String> contentSupplier) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            String content = contentSupplier.get();
            writer.write(content);
        } catch (IOException e) {
        }
    }

    public static String readFromFile(String fileName) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
        }
        return content.toString();
    }
}

class PerpustakaanGUI {
    private JFrame frame;
    private JPanel panelTampilan1;
    private JPanel panelTampilan2;
    private JPanel panelTampilan3;
    private JTextField fieldNamaPeminjam;
    private JTextField fieldNbiPeminjam;
    private JTextField fieldJudulBuku;
    private JTextField fieldTanggalPeminjaman;
    private JTextArea areaOutput;
    private JButton tombolMasukUntukMeminjam;
    private JButton tombolPinjam;

    public PerpustakaanGUI() {
        frame = new JFrame("Perpustakaan");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Tampilan 1
        JLabel labelNamaPeminjam = new JLabel("Nama Peminjam:");
        JLabel labelNbiPeminjam = new JLabel("NBI Peminjam:");
        fieldNamaPeminjam = new JTextField(20);
        fieldNbiPeminjam = new JTextField(10);
        tombolMasukUntukMeminjam = new JButton("Masuk untuk Meminjam");

        panelTampilan1 = new JPanel(new GridLayout(3, 2));
        panelTampilan1.add(labelNamaPeminjam);
        panelTampilan1.add(fieldNamaPeminjam);
        panelTampilan1.add(labelNbiPeminjam);
        panelTampilan1.add(fieldNbiPeminjam);
        panelTampilan1.add(new JLabel());
        panelTampilan1.add(tombolMasukUntukMeminjam);

        // Tampilan 2
        JLabel labelJudulBuku = new JLabel("Judul Buku:");
        JLabel labelTanggalPeminjaman = new JLabel("Tanggal Peminjaman (dd/MM/yyyy):");
        fieldJudulBuku = new JTextField(20);
        fieldTanggalPeminjaman = new JTextField(10);
        tombolPinjam = new JButton("Pinjam");

        panelTampilan2 = new JPanel(new GridLayout(3, 2));
        panelTampilan2.add(labelJudulBuku);
        panelTampilan2.add(fieldJudulBuku);
        panelTampilan2.add(labelTanggalPeminjaman);
        panelTampilan2.add(fieldTanggalPeminjaman);
        panelTampilan2.add(new JLabel());
        panelTampilan2.add(tombolPinjam);

        // Tampilan 3
        areaOutput = new JTextArea(5, 30);
        areaOutput.setEditable(true);

        panelTampilan3 = new JPanel();
        panelTampilan3.add(new JScrollPane(areaOutput));

        // Mengatur tampilan awal
        panelTampilan2.setVisible(false);
        
        panelTampilan3.setVisible(true);

        // Mengatur aksi tombol Masuk untuk Meminjam
        tombolMasukUntukMeminjam.addActionListener((ActionEvent e) -> {
            panelTampilan1.setVisible(false);
            panelTampilan2.setVisible(true);
        });

        // Mengatur aksi tombol Pinjam
        tombolPinjam.addActionListener((ActionEvent e) -> { //lamda expession untuk mengimplementasikan 
            String namaPeminjam = fieldNamaPeminjam.getText(); //ActionListener pada tombol tombolPinjam
            String nbiPeminjam = fieldNbiPeminjam.getText();
            String judulBuku = fieldJudulBuku.getText();
            String tanggalPeminjamanStr = fieldTanggalPeminjaman.getText();
            
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date tanggalPeminjaman = null;
            try {
                tanggalPeminjaman = dateFormat.parse(tanggalPeminjamanStr);
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(frame, "Format tanggal tidak valid!", "Kesalahan", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Peminjam peminjam = new Peminjam(namaPeminjam, nbiPeminjam);
            Item item = new Book(judulBuku, "Berhasil");
            
            Pinjaman pinjaman = new Pinjaman(peminjam, item, tanggalPeminjaman);
            pinjaman.getItem().displayInfo();
            
            areaOutput.append("Peminjam: " + pinjaman.getPeminjam().getNama() + "\n");
            areaOutput.append("NBI: " + pinjaman.getPeminjam().getNbi() + "\n");
            areaOutput.append("Judul Buku: " + pinjaman.getItem().getTitle() + "\n");
            areaOutput.append("Tanggal Peminjaman: " + dateFormat.format(pinjaman.getTanggalPeminjaman()) + "\n");
            areaOutput.append("Tanggal Pengembalian: " + dateFormat.format(pinjaman.getTanggalPengembalian()) + "\n");
            areaOutput.append("\n");
        });

        frame.add(panelTampilan1, BorderLayout.NORTH);
        frame.add(panelTampilan2, BorderLayout.CENTER);
        frame.add(panelTampilan3, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }
}

public class Main {
    public static void main(String[] args) { //objek
        SwingUtilities.invokeLater(() -> {
            new PerpustakaanGUI();
        });
    }
}
