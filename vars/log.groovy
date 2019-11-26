import com.perficient.devops.log.*

def call( message ){

  def formattedMessage = PerficientMessage.formattedMessage(message)
  echo "log:: ${formattedMessage}"
}
