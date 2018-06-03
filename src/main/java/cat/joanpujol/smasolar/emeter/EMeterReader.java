package cat.joanpujol.smasolar.emeter;

import cat.joanpujol.smasolar.emeter.impl.EMeterCreateObservableImpl;
import io.reactivex.Observable;

/**
 * Subscribes to EMETER multicast group and contiously retrieve and prints to console it's lectures
 */
public class EMeterReader {

  private EMeterConfig config;

  public EMeterReader() {
    this(EMeterConfig.newBuilder().build());
  }

  public EMeterReader(EMeterConfig config) {
    this.config = config;
  }

  /**
   * Creates a cold observable that starts to listens to EMeter lectures on first subscription. The
   * observable can be shared to multiple subscribers that will receive same results without
   * creating multiple sockets. But when last subscriber finish listening internal ressources are
   * released and observable is completed
   */
  public final Observable<EMeterLecture> create() {
    return Observable.create(createObservable()).share();
  }

  // Exposed only for testing purpouses
  protected EMeterCreateObservableImpl createObservable() {
    return new EMeterCreateObservableImpl(config);
  }

  protected final EMeterConfig getConfig() {
    return config;
  }

  public static void main(String[] args) {
    new EMeterReader().create().subscribe(lecture -> System.out.println(lecture));
  }
}
