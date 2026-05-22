import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MatrisEkrani {
    private JFrame cerceve;
    private JTextField boyutGirdisi;
    private JComboBox<String> secimKutusu;

    // Matrisleri metotlar arasında taşıyabilmek için sınıf seviyesinde tanımlıyoruz
    private int[][] matrix1;
    private int[][] matrix2;
    private int matrisBoyutu;

    public MatrisEkrani() {
        cerceve = new JFrame("Kare Matris Çarpımı");
        cerceve.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        cerceve.setSize(350, 250);
        cerceve.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));

        ekraniOlustur();
    }

    private void ekraniOlustur() {
        cerceve.add(new JLabel("Matris Boyutunu giriniz:"));
        boyutGirdisi = new JTextField(10);
        cerceve.add(boyutGirdisi);

        String[] secenekler = {"Değerleri elle dolduracağım", "Rastgele sayılarla doldur"};
        secimKutusu = new JComboBox<>(secenekler);
        cerceve.add(secimKutusu);

        JButton ilerleButonu = new JButton("İlerle");
        cerceve.add(ilerleButonu);

        ilerleButonu.addActionListener(e -> ilerleButonunaTiklandi());

        cerceve.setLocationRelativeTo(null);
        cerceve.setVisible(true);
    }

    private void ilerleButonunaTiklandi() {
        String girilenDeger = boyutGirdisi.getText();

        try {
            matrisBoyutu = Integer.parseInt(girilenDeger);

            if (matrisBoyutu <= 0) {
                JOptionPane.showMessageDialog(cerceve, "Matris boyutu 0'dan büyük olmalıdır!", "Hatalı Giriş", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Dizilerimizi boyuta göre başlatıyoruz. (Hesaplama kolaylığı için int[][] kullanmak daha sağlıklıdır)
            matrix1 = new int[matrisBoyutu][matrisBoyutu];
            matrix2 = new int[matrisBoyutu][matrisBoyutu];

            String secilenYontem = (String) secimKutusu.getSelectedItem();

            if ("Değerleri elle dolduracağım".equals(secilenYontem)) {
                // Zincirin ilk halkasını başlatıyoruz: 1. Matrisi doldur!
                elleDoldur(1);
            } else {
                // Rastgele ise ekran çizmeyle uğraşmadan anında doldurup sonuca zıplayabiliriz
                Random rnd = new Random();
                for (int i = 0; i < matrisBoyutu; i++) {
                    for (int j = 0; j < matrisBoyutu; j++) {
                        matrix1[i][j] = rnd.nextInt(10); // Çok büyük sayılar çıkmasın diye 10 yaptık
                        matrix2[i][j] = rnd.nextInt(10);
                    }
                }
                sonuclariHesaplaVeGoster();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(cerceve, "Lütfen geçerli bir tam sayı giriniz!", "Hatalı Giriş", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Bu metot artık sadece ekranı çizer ve buton tıklandığında ne olacağını belirler
    private void elleDoldur(int sira) {
        cerceve.getContentPane().removeAll();
        cerceve.setLayout(new GridLayout(matrisBoyutu + 2, matrisBoyutu, 5, 5));

        cerceve.add(new JLabel(sira + ". Matris Elemanlarını Giriniz:"));
        for (int i = 1; i < matrisBoyutu; i++) cerceve.add(new JLabel(""));

        JTextField[][] kutular = new JTextField[matrisBoyutu][matrisBoyutu];

        for (int i = 0; i < matrisBoyutu; i++) {
            for (int j = 0; j < matrisBoyutu; j++) {
                JTextField degerGirdisi = new JTextField(5);
                degerGirdisi.setToolTipText((i + 1) + ". Satır, " + (j + 1) + ". Sütun");
                cerceve.add(degerGirdisi);
                kutular[i][j] = degerGirdisi;
            }
        }

        JButton kaydetButonu = new JButton(sira + ". Matrisi Kaydet");
        cerceve.add(kaydetButonu);

        kaydetButonu.addActionListener(e -> {
            try {
                // Hangi matristeysek onun içine kayıt yapıyoruz
                int[][] aktifMatris = (sira == 1) ? matrix1 : matrix2;

                for (int i = 0; i < matrisBoyutu; i++) {
                    for (int j = 0; j < matrisBoyutu; j++) {
                        String yazi = kutular[i][j].getText();
                        aktifMatris[i][j] = Integer.parseInt(yazi);
                    }
                }

                // ZİNCİRLEME MANTIĞI BURADA:
                if (sira == 1) {
                    // 1. bittiyse 2'yi çağır
                    elleDoldur(2);
                } else {
                    // 2. de bittiyse hesaplamaya geç
                    sonuclariHesaplaVeGoster();
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(cerceve, "Lütfen tüm kutulara geçerli bir sayı giriniz!", "Hatalı Giriş", JOptionPane.ERROR_MESSAGE);
            }
        });

        cerceve.revalidate();
        cerceve.repaint();
    }

    private void sonuclariHesaplaVeGoster() {
        int[][] sonucMatrisi = matrisCarpimi(matrix1, matrix2);

        cerceve.getContentPane().removeAll();

        cerceve.setLayout(new BorderLayout(10, 10));

        JLabel baslik = new JLabel("Çarpım Sonucu:", SwingConstants.CENTER);
        baslik.setFont(new Font("Arial", Font.BOLD, 16));
        cerceve.add(baslik, BorderLayout.NORTH);

        JPanel matrisPaneli = new JPanel(new GridLayout(matrisBoyutu, matrisBoyutu, 5, 5));
        for (int i = 0; i < matrisBoyutu; i++) {
            for (int j = 0; j < matrisBoyutu; j++) {
                JTextField sonucHucresi = new JTextField(String.valueOf(sonucMatrisi[i][j]));
                sonucHucresi.setEditable(false);
                sonucHucresi.setHorizontalAlignment(JTextField.CENTER);
                sonucHucresi.setFont(new Font("Arial", Font.BOLD, 14));
                matrisPaneli.add(sonucHucresi);
            }
        }
        cerceve.add(matrisPaneli, BorderLayout.CENTER);

        // GÜNEY: Butonlar Paneli (Yan yana dizilmeleri için FlowLayout kullanıyoruz)
        JPanel butonPaneli = new JPanel(new FlowLayout());

        JButton matrisleriGorButonu = new JButton("Girilen Matrisleri Gör");
        JButton yenidenCalistir = new JButton("Programı Yeniden Çalıştır");

        butonPaneli.add(matrisleriGorButonu);
        butonPaneli.add(yenidenCalistir);
        cerceve.add(butonPaneli, BorderLayout.SOUTH);

        matrisleriGorButonu.addActionListener(e -> girilenMatrisleriGosterEkrani());
        yenidenCalistir.addActionListener(e -> {
            cerceve.dispose();
            MatrisEkrani.main(new String[2]);
        });

        cerceve.revalidate();
        cerceve.repaint();
        cerceve.pack(); // Ekranı içeriğe göre sıkılaştırır
        cerceve.setLocationRelativeTo(null);
    }

    // "Girilen Matrisleri Gör" butonuna tıklandığında açılacak pop-up pencere
    private void girilenMatrisleriGosterEkrani() {
        // JDialog, JFrame'in üzerine açılan ve kapanana kadar alt ekranı donduran penceredir
        JDialog matrisDialog = new JDialog(cerceve, "Girilen Matrisler", true);

        // İki matrisi yan yana göstermek için 1 satır, 2 sütunlu bir düzen
        matrisDialog.setLayout(new GridLayout(1, 2, 20, 10));

        // Metodumuzu kullanarak iki matris için de görsel panel oluşturup Dialog'a ekliyoruz
        matrisDialog.add(matrisGorseliOlustur("1. Matris", matrix1));
        matrisDialog.add(matrisGorseliOlustur("2. Matris", matrix2));

        // Pencereyi ekrana oturtup görünür yapıyoruz
        matrisDialog.pack();
        matrisDialog.setLocationRelativeTo(cerceve);
        matrisDialog.setVisible(true);
    }

    // Kod tekrarını önlemek için matris çizimini ayrı bir metoda aldık
    private JPanel matrisGorseliOlustur(String baslik, int[][] matris) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(new JLabel(baslik, SwingConstants.CENTER), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(matrisBoyutu, matrisBoyutu, 2, 2));
        for (int i = 0; i < matrisBoyutu; i++) {
            for (int j = 0; j < matrisBoyutu; j++) {
                JTextField hucre = new JTextField(String.valueOf(matris[i][j]), 3);
                hucre.setEditable(false); // Sadece okunabilir
                hucre.setHorizontalAlignment(JTextField.CENTER);
                grid.add(hucre);
            }
        }
        panel.add(grid, BorderLayout.CENTER);
        return panel;
    }

    private int[][] matrisCarpimi(int[][] mat1, int[][] mat2) {
        int[][] matrixTime = new int[matrisBoyutu][matrisBoyutu];
        for (int i = 0; i < matrisBoyutu; i++) {
            for (int j = 0; j < matrisBoyutu; j++) {
                matrixTime[i][j] = 0; // Toplama işleminden önce null hatası almamak için sıfırlıyoruz
                for (int k = 0; k < matrisBoyutu; k++) {
                    matrixTime[i][j] += mat1[i][k] * mat2[k][j];
                }
            }
        }
        return matrixTime;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MatrisEkrani());
    }
}